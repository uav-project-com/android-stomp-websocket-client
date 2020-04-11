package ua.naiksoftware.stompclientexample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.CompletableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.dto.StompHeader;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stompclientexample.adapter.SimpleAdapter;
import ua.naiksoftware.stompclientexample.callback.RestfulCallback;
import ua.naiksoftware.stompclientexample.model.Command;
import ua.naiksoftware.stompclientexample.model.UserModel;
import ua.naiksoftware.stompclientexample.network.ApiClient;
import ua.naiksoftware.stompclientexample.network.Constant;
import ua.naiksoftware.stompclientexample.network.RestClient;
import ua.naiksoftware.stompclientexample.resource.AuthenticationController;
import ua.naiksoftware.stompclientexample.resource.implement.AuthenticationControllerIpml;

import static ua.naiksoftware.stompclientexample.network.Constant.BASE_PORT;
import static ua.naiksoftware.stompclientexample.network.Constant.BASE_URL_SERVER;

public class MainActivity extends AppCompatActivity implements RestfulCallback {

    private static final String TAG = "MainActivity";

    private SimpleAdapter mAdapter;
    private List<String> mDataSet = new ArrayList<>();
    private StompClient mStompClient;
    private Disposable mRestPingDisposable;
    private final SimpleDateFormat mTimeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private RecyclerView mRecyclerView;
    private Gson mGson = new GsonBuilder().create();

    private CompositeDisposable compositeDisposable;

    private String token;
    private AuthenticationControllerIpml authenticationController;

    private final String fromUser = "admin";
    private final String toUser = "client";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button chat = findViewById(R.id.chattingBtn);
        chat.setText(chat.getText() + " " + toUser);
        AuthenticationController service = ApiClient.getClient().create(AuthenticationController.class);
        authenticationController = new AuthenticationControllerIpml(service);
        authenticationController.setCallback(this);
        authenticationController.login(new UserModel()
                .setUsername(fromUser)
                .setPassword(fromUser));
        Toast.makeText(this, "Authen..", Toast.LENGTH_SHORT).show();

        mRecyclerView = findViewById(R.id.recycler_view);
        mAdapter = new SimpleAdapter(mDataSet);
        mAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true));

        // must: "ws://" and end with: /websocket
        mStompClient = Stomp.over(
                Stomp.ConnectionProvider.OKHTTP,
                "ws://" + BASE_URL_SERVER +":" + BASE_PORT + "/example-endpoint/websocket");

        resetSubscriptions();
    }

    public void disconnectStomp(View view) {
        mStompClient.disconnect();
    }

    public void connectStomp(View view) {

        List<StompHeader> headers = new ArrayList<>();
        // add token that was authenticated
        headers.add(new StompHeader(Constant.AUTH, this.token));

        mStompClient.withClientHeartbeat(1000).withServerHeartbeat(1000);

        resetSubscriptions();

        Disposable dispLifecycle = mStompClient.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case OPENED:
                            toast("Stomp connection opened");
                            break;
                        case ERROR:
                            Log.e(TAG, "Stomp connection error", lifecycleEvent.getException());
                            toast("Stomp connection error");
                            break;
                        case CLOSED:
                            toast("Stomp connection closed");
                            resetSubscriptions();
                            break;
                        case FAILED_SERVER_HEARTBEAT:
                            toast("Stomp failed server heartbeat");
                            break;
                    }
                });

        compositeDisposable.add(dispLifecycle);

        // Receive greetings
        Disposable dispTopic = mStompClient.topic("/topic/greetings") // subscribe
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.e(TAG, "Received " + topicMessage.getPayload());
                    addItem(mGson.fromJson(topicMessage.getPayload(), Command.class));
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        // subscribe my username topic for received message from other user: sendCommandToUser()
        String subscribePath = "/chat/queue/" + fromUser + "/chat/queue/messages";
        Log.e("Subscribe", subscribePath);
        Disposable usersTopic = mStompClient.topic(subscribePath) // subscribe
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(topicMessage -> {
                    Log.e(TAG, "Received from USER: " + topicMessage.getPayload());
                    addItem(mGson.fromJson(topicMessage.getPayload(), Command.class));
                }, throwable -> Log.e(TAG, "Error on subscribe topic", throwable));

        compositeDisposable.add(dispTopic);
        compositeDisposable.add(usersTopic);

        mStompClient.connect(headers);
    }

    public void sendEchoViaStomp(View v) {
        Command sendMsg = new Command().setFromUser(fromUser).setData("sendEchoViaStomp");
        compositeDisposable.add(mStompClient.send("/app/hello-msg-mapping", new Gson().toJson(sendMsg))
                .compose(applySchedulers())
                .subscribe(() -> Log.e(TAG, "STOMP echo send successfully"), throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    toast(throwable.getMessage());
                })

        );
    }

    /**
     *  Send to special user, test by swap fromUser - toUser user with two devices
     * @param v view
     */
    public void sendCommandToUser(View v) {
        Command command = new Command()
                .setFromUser(fromUser)
                .setToUser(toUser)
                .setData("Echo STOMP " + mTimeFormat.format(new Date()));
        String data = new Gson().toJson(command);
        Log.e("Sending command", "Command: " + data);
        compositeDisposable.add(mStompClient.send("/app/commander", data)
                .compose(applySchedulers())
                .subscribe(() -> Log.e(TAG, "STOMP echo send successfully"), throwable -> {
                    Log.e(TAG, "Error send STOMP echo", throwable);
                    toast(throwable.getMessage());
                })

        );
    }

    public void sendEchoViaRest(View v) {
        mRestPingDisposable = RestClient.getInstance().getExampleRepository()
                .sendRestEcho(Constant.TOKEN_HEADER + token, "Echo REST " + mTimeFormat.format(new Date()))
                .compose(applySchedulers())
                .subscribe(() -> Log.e(TAG, "REST echo send successfully"), throwable -> {
                    Log.e(TAG, "Error send REST echo", throwable);
                    toast(throwable.getMessage());
                });
    }

    private void addItem(Command msg) {
        mDataSet.add("[" + msg.getFromUser() + "] " + msg.getData() + " - " + mTimeFormat.format(new Date()));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mDataSet.size() - 1);
    }

    private void toast(String text) {
        Log.i(TAG, text);
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    protected CompletableTransformer applySchedulers() {
        return upstream -> upstream
                .unsubscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void resetSubscriptions() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
        }
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    protected void onDestroy() {
        mStompClient.disconnect();

        if (mRestPingDisposable != null) mRestPingDisposable.dispose();
        if (compositeDisposable != null) compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void passCallbackData(Object data) {
        this.token = (String) data;
        Log.e("Token", "Token: " + token);
    }
}
