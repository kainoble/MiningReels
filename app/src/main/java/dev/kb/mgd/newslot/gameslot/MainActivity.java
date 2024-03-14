package dev.kb.mgd.newslot.gameslot;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import dev.kb.mgd.newslot.R;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startActivity(new Intent(MainActivity.this, GameView.class));
        finish();
    }


}
