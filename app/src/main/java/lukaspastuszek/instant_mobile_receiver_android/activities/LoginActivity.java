package lukaspastuszek.instant_mobile_receiver_android.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import lukaspastuszek.instant_mobile_receiver_android.R;
import lukaspastuszek.instant_mobile_receiver_android.components.CustomToast;
import lukaspastuszek.instant_mobile_receiver_android.interfaces.LoginCallbackInterface;
import lukaspastuszek.instant_mobile_receiver_android.resources.AuthorizationResource;
import lukaspastuszek.instant_mobile_receiver_android.resources.LocalStorageResource;

import static android.R.attr.duration;
import static android.R.attr.password;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class LoginActivity extends AppCompatActivity implements LoginCallbackInterface {

    LoginCallbackInterface loginCallbackInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent ii = getIntent();

        // find out if the user is logged in
        LocalStorageResource localStorageResource = new LocalStorageResource(this);
        if (localStorageResource.isBearerTokenSet()) {
            navigateToHomepageActivity();
        }

        loginCallbackInterface = this;

        Button loginBtn = (Button) this.findViewById(R.id.buttonLogin);
        final EditText emailEditText = (EditText) this.findViewById(R.id.editTextEmail);
        final EditText passwordEditText = (EditText) this.findViewById(R.id.editTextPassword);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (email == null || email.isEmpty()) {
                    CustomToast.construct(getApplicationContext(), getLayoutInflater(), "Email can not be empty", CustomToast.ToastType.ERROR).show();
                    return;
                }

                boolean emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if (!emailValid) {
                    CustomToast.construct(getApplicationContext(), getLayoutInflater(), "Please, enter valid email", CustomToast.ToastType.ERROR).show();
                    return;
                }


                if (password == null || password.isEmpty()) {
                    CustomToast.construct(getApplicationContext(), getLayoutInflater(), "Password can not be empty", CustomToast.ToastType.ERROR).show();
                    return;
                }

                AuthorizationResource authResource = new AuthorizationResource(getApplicationContext());
                authResource.sendLoginRequest(loginCallbackInterface, email, password);
            }
        });
    }

    @Override
    public void onLoginRequestSuccess(String token) {

        saveTokenToLocalStorage(token);

        navigateToHomepageActivity();

    }

    private void saveTokenToLocalStorage(String token) {

        LocalStorageResource localStorageResource = new LocalStorageResource(this);
        localStorageResource.putBearerToken(token);

    }

    private void navigateToHomepageActivity() {

        Intent intent = new Intent(this, HomepageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
