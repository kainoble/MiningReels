package dev.kb.mgd.newslot.gameslot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

import dev.kb.mgd.newslot.R;

public class GameView extends AppCompatActivity {

    private static final int MULTIPLY_BY_1 = 7;
    private static final int MULTIPLY_BY_2 = 72;
    private static final int MULTIPLY_BY_3 = 142;
    private static final int MULTIPLY_BY_4 = 212;

    // Constants for adding slot values
    private int addBy1 = 5;
    private int addBy2 = 5;
    private int addBy3 = 5;

    // Array to hold slot values
    private final int[] slot = {1, 2, 3, 4, 5, 6, 7};

    // RecyclerView instances for each slot
    private RecyclerView itemView1;
    private RecyclerView itemView2;
    private RecyclerView itemView3;

    // CustomManager instances for each slot
    private LayoutManager Custom1;
    private LayoutManager Custom2;
    private LayoutManager Custom3;

    // TextView instances for energy price, my power, and to play
    private TextView energyPrice;
    private TextView myPower;
    private TextView toPlay;

    // Values for my coins, play, and fortune
    private int myCoinsVal;
    private int playVal;
    private int fortuneVal;

    // Boolean to check if it's the first run of the app
    private boolean firstRun;

    // Boolean to check if the wheel is spinning
    private boolean isSpinning = false;

    // Game logic instance
    private GameMechanics gameLogic;

    // SharedPreferences instance
    private SharedPreferences prefs;

    // MediaPlayer instances for music and sound effects
    private MediaPlayer musicPlayer;
    private MediaPlayer winSound;
    private MediaPlayer plusMinustone;
    private MediaPlayer backgroundSound;

    // Constants for SharedPreferences
    private static final String PREFS_NAME = "FirstRun";

    // Values for music and sound effects
    private int playMusic;
    private int playSound;

    // ImageViews for settings dialog
    private ImageView musicOff;
    private ImageView musicOn;
    private ImageView soundOn;
    private ImageView soundOff;
    private GameMechanics gameScheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameview);

        ImageButton btn_min;
        ImageButton btn_max;
        SpinnerAdapter adapter;
        ImageView settingsButton;
        ImageView mechanicsView;
        ImageButton spinButton;

        backgroundSound = MediaPlayer.create(this, R.raw.background_sound);
        backgroundSound.setLooping(true);
        musicPlayer = MediaPlayer.create(this, R.raw.tone_spin);
        winSound = MediaPlayer.create(this, R.raw.win);
        plusMinustone = MediaPlayer.create(this, R.raw.button_tone);

        prefs = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        firstRun = prefs.getBoolean("firstRun", true);

        if (firstRun) {
            playMusic = 1;
            playSound = 1;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            playMusic = prefs.getInt("music", 1);
            playSound = prefs.getInt("sound", 1);
            checkMusic();
        }

        gameLogic = new GameMechanics();
        settingsButton = findViewById(R.id.btn_settings);
        mechanicsView = findViewById(R.id.btn_mechanics);
        spinButton = findViewById(R.id.btn_spin);
        btn_max = findViewById(R.id.btn_max);
        btn_min = findViewById(R.id.btn_min);
        energyPrice = findViewById(R.id.fortuneprice);
        myPower = findViewById(R.id.energy);
        toPlay = findViewById(R.id.bet);
        adapter = new SpinnerAdapter(this, slot, gameScheme);

        itemView1 = findViewById(R.id.spinner1);
        itemView2 = findViewById(R.id.spinner2);
        itemView3 = findViewById(R.id.spinner3);
        itemView1.setHasFixedSize(true);
        itemView2.setHasFixedSize(true);
        itemView3.setHasFixedSize(true);

        Custom1 = new LayoutManager(this);
        Custom1.setScrollEnabled(false);
        itemView1.setLayoutManager(Custom1);
        Custom2 = new LayoutManager(this);
        Custom2.setScrollEnabled(false);
        itemView2.setLayoutManager(Custom2);
        Custom3 = new LayoutManager(this);
        Custom3.setScrollEnabled(false);
        itemView3.setLayoutManager(Custom3);

        itemView1.setAdapter(adapter);
        itemView2.setAdapter(adapter);
        itemView3.setAdapter(adapter);
        itemView1.scrollToPosition(addBy1);
        itemView2.scrollToPosition(addBy2);
        itemView3.scrollToPosition(addBy3);

        setText();
        updateText();

        mechanicsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inflate the XML layout dynamically
                LayoutInflater inflater = LayoutInflater.from(GameView.this);
                View layout = inflater.inflate(R.layout.prices, null);

                // Display the inflated layout in a dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(GameView.this);
                builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        itemView1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    itemView1.scrollToPosition(gameLogic.getPosition(0));
                    Custom1.setScrollEnabled(false);
                }
            }
        });

        itemView2.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    itemView2.scrollToPosition(gameLogic.getPosition(1));
                    Custom2.setScrollEnabled(false);
                }
            }
        });
        itemView3.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    itemView3.scrollToPosition(gameLogic.getPosition(2));
                    Custom3.setScrollEnabled(false);
                    updateText();
                    if (gameLogic.getHasWon()) {
                        if (playSound == 1) {
                            winSound.start();
                        }
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.activity_pop_message, findViewById(R.id.win_splash));
                        TextView winCoins = layout.findViewById(R.id.win_coins);
                        winCoins.setText(gameLogic.getPrize());
                        Toast toast = new Toast(GameView.this);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                        toast.setView(layout);
                        toast.show();
                        gameLogic.setHasWon(false);
                    }
                    isSpinning = false;
                    spinButton.setEnabled(true);
                }
            }
        });

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);
            if (!isSpinning) {
                if (playSound == 1) {
                    musicPlayer.start();
                }
                isSpinning = true;
            }
            Custom1.setScrollEnabled(true);
            Custom2.setScrollEnabled(true);
            Custom3.setScrollEnabled(true);
            gameLogic.getSpinResults();
            addBy1 = gameLogic.getPosition(0) + MULTIPLY_BY_2;
            addBy2 = gameLogic.getPosition(1) + MULTIPLY_BY_3;
            addBy3 = gameLogic.getPosition(2) + MULTIPLY_BY_4;
            itemView1.smoothScrollToPosition(addBy1);
            itemView2.smoothScrollToPosition(addBy2);
            itemView3.smoothScrollToPosition(addBy3);

            startSpinAnimation(spinButton);
        });

        btn_max.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinustone.start();
            }
            gameLogic.betUp();
            updateText();
        });

        btn_min.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinustone.start();
            }
            gameLogic.betDown();
            updateText();
        });

        settingsButton.setOnClickListener(v -> {
            if (playSound == 1) {
                plusMinustone.start();
            }
            showSettingsDialog();
        });
    }

    private void startSpinAnimation(View view) {
        Animation scaleAnimation = AnimationUtils.loadAnimation(GameView.this, R.anim.animation);
        view.startAnimation(scaleAnimation);
    }

    private void setText() {
        if (firstRun) {
            gameLogic.setMyCoins(1000);
            gameLogic.setBet(5);
            gameLogic.setJackpot(100000);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstRun", false);
            editor.apply();
        } else {
            String coins = prefs.getString("coins", "1000");
            String myPlay = prefs.getString("play", "5");
            String jackpot = prefs.getString("jackpot", "100000");
            Log.d("COINS", coins);
            myCoinsVal = Integer.parseInt(coins);
            playVal = Integer.parseInt(myPlay);
            fortuneVal = Integer.parseInt(jackpot);
            gameLogic.setMyCoins(myCoinsVal);
            gameLogic.setBet(playVal);
            gameLogic.setJackpot(fortuneVal);
        }
    }

    private void updateText() {
        energyPrice.setText(gameLogic.getJackpot());
        myPower.setText(gameLogic.getMyCoins());
        toPlay.setText(gameLogic.getBet());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("coins", gameLogic.getMyCoins());
        editor.putString("play", gameLogic.getBet());
        editor.putString("jackpot", gameLogic.getJackpot());
        editor.apply();
    }

    private static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;

        public ItemViewHolder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.slot_item);
        }
    }

    private class SpinnerAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        public SpinnerAdapter(GameView slotMachineActivity, int[] slot, GameMechanics gameLogic) {
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(GameView.this);
            View view = layoutInflater.inflate(R.layout.slot_item, parent, false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            int i = position < 7 ? position : position % MULTIPLY_BY_1;
            switch (slot[i]) {
                case 1:
                    holder.pic.setImageResource(R.drawable.box_gold);
                    break;
                case 2:
                    holder.pic.setImageResource(R.drawable.coins);
                    break;
                case 3:
                    holder.pic.setImageResource(R.drawable.gold);
                    break;
                case 4:
                    holder.pic.setImageResource(R.drawable.bomb);
                    break;
                case 5:
                    holder.pic.setImageResource(R.drawable.balb);
                    break;
                case 6:
                    holder.pic.setImageResource(R.drawable.diamond);
                    break;
                case 7:
                    holder.pic.setImageResource(R.drawable.sword);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }
    }


    private void showSettingsDialog() {
        final Dialog dialog;
        dialog = new Dialog(this, R.style.WinDialog);

        Objects.requireNonNull(dialog.getWindow()).setContentView(R.layout.activity_settings);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        dialog.setCancelable(false);



        ImageView close = dialog.findViewById(R.id.close);
        close.setOnClickListener(v -> dialog.dismiss());

        musicOn = dialog.findViewById(R.id.music_on);
        musicOn.setOnClickListener(v -> {
            playMusic = 0;
            checkMusic();
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("music", playMusic);
            editor.apply();
        });

        musicOff = dialog.findViewById(R.id.music_off);
        musicOff.setOnClickListener(v -> {
            playMusic = 1;
            backgroundSound.start();
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("music", playMusic);
            editor.apply();
        });

        soundOn = dialog.findViewById(R.id.sounds_on);
        soundOn.setOnClickListener(v -> {
            playSound = 0;
            checkSoundDraw();
            soundOn.setVisibility(View.INVISIBLE);
            soundOff.setVisibility(View.VISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sound", playSound);
            editor.apply();
        });

        soundOff = dialog.findViewById(R.id.sounds_off);
        soundOff.setOnClickListener(v -> {
            playSound = 1;
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.INVISIBLE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("sound", playSound);
            editor.apply();
        });

        checkMusicDraw();
        checkSoundDraw();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        backgroundSound.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkMusic();
    }

    private void checkMusic() {
        if (playMusic == 1) {
            backgroundSound.start();
        } else {
            backgroundSound.pause();
        }
    }

    private void checkMusicDraw() {
        if (playMusic == 1) {
            musicOn.setVisibility(View.VISIBLE);
            musicOff.setVisibility(View.INVISIBLE);
        } else {
            musicOn.setVisibility(View.INVISIBLE);
            musicOff.setVisibility(View.VISIBLE);
        }
    }

    private void checkSoundDraw() {
        if (playSound == 1) {
            soundOn.setVisibility(View.VISIBLE);
            soundOff.setVisibility(View.INVISIBLE);
        } else {
            soundOn.setVisibility(View.INVISIBLE);
            soundOff.setVisibility(View.VISIBLE);
        }
    }
}