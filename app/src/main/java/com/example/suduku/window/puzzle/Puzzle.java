package com.example.suduku.window.puzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.ValueAnimator;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.suduku.R;
import com.example.suduku.Solution;
import com.example.suduku.dialog.PauseDialog;
import com.example.suduku.dialog.WinDialog;
import com.example.suduku.window.puzzle.countdowntimer.CountDownTimerSupport;
import com.example.suduku.window.puzzle.countdowntimer.OnCountDownTimerListener;
import com.example.suduku.dialog.LostDialog;

import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Puzzle extends AppCompatActivity {
    //LOG
    private static final String TAG = "Puzzle";

    private static final Float NOT_FILL_STATUS = 0.35f;
    private static final Float GAME_STATUS = 1f;
    private static final Float SIGN_STATUS = 0.75f;

    public PuzzleView puzzleV;
    ImageView[] operation;
    public CountDownTimerSupport mTimer;
    public TextView puzzle_top_text;

    TreeMap<Integer, int[][]> noteMap = new TreeMap<Integer, int[][]>();
    TreeMap<Integer, boolean[][][]> signMap = new TreeMap<Integer, boolean[][][]>();
    int key = 0;//当前key
    int keyMax = 0;//历史key最大值
    int revokeMax = 20;//撤销最多次数

    SoundPool soundPool;//声音池
    int soundID;//声音id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        soundPool = new SoundPool.Builder().build();
        soundID = soundPool.load(this, R.raw.popup_appear, 1);

        //接收传递的数据
        Bundle bundle = getIntent().getExtras();
        String level = bundle.getString("level");
        int levelNum = Integer.parseInt(level.substring(level.length() - (level.length() - 7)));
        Log.d(TAG, String.valueOf(levelNum));

        getSupportActionBar().hide();//去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉虚拟按键
        setContentView(R.layout.activity_puzzle);//加载布局文件

        puzzle_top_text = findViewById(R.id.puzzle_top_text);//获取顶部文本框
        puzzle_top_text.setText(level);//设置顶部文本框的文字
        ImageView pause = findViewById(R.id.pause); //暂停按钮
        puzzleV = findViewById(R.id.puzzle);//拼图游戏界面
        puzzleV.level = levelNum - 1;
        TextView timeCount = findViewById(R.id.timecount);//时间倒计时
        ProgressBar progressBar = findViewById(R.id.progressBar);//时间倒计时进度条
        LostDialog lostDialog = new LostDialog(this);//游戏失败对话框
        lostDialog.mPuzzle = this;
        ConstraintLayout operationPanel = findViewById(R.id.OperationPanel);//操作面板
        ImageView sign = findViewById(R.id.sign);//标记按钮
        ImageView revoke = findViewById(R.id.revoke);//撤销按钮

        operation = new ImageView[]{sign, findViewById(R.id.operation01), findViewById(R.id.operation02), findViewById(R.id.operation03),
                findViewById(R.id.operation04), findViewById(R.id.operation05), findViewById(R.id.operation06),
                findViewById(R.id.operation07), findViewById(R.id.operation08), findViewById(R.id.operation09), revoke};//操作面板按钮数组

        for (ImageView iv : operation) {
            iv.setAlpha(NOT_FILL_STATUS);//设置按钮透明度
        }

        //puzzleV被触摸时,设置操作面板按钮透明度
        puzzleV.setOnTouchListener((v, event) -> {
            Log.d(TAG, "onTouch");
            setButtonColor();
            return false;   //返回false，不消费此次事件
        });

        mTimer = new CountDownTimerSupport(30 * 60 * 1000, 1000);//倒计时总时长10分钟，间隔1秒
        mTimer.setOnCountDownTimerListener(new OnCountDownTimerListener() {
            @Override
            public void onTick(long millisUntilFinished) {
                String str2 = String.valueOf(((millisUntilFinished / 1000) % 60));
                if (((millisUntilFinished / 1000) % 60) < 10) {
                    str2 = "0" + (millisUntilFinished / 1000) % 60;//设置10秒之后的显示形式09
                }
                timeCount.setText((millisUntilFinished / 60 / 1000) + ":" + str2);
                //时间显示出来
                progressBar.setProgress((int) millisUntilFinished / 100 / 60);
            }

            @Override
            public void onFinish() {
                timeCount.setText((0 + ":" + "00"));
                progressBar.setProgress(0);//设置进度条
                lostDialog.show();//显示失败的dialog
            }

            @Override
            public void onCancel() {
                //手动结束
            }
        });
        mTimer.start();//别忘了开始哦

        PauseDialog pauseDialog = new PauseDialog(this);//暂停对话框
        pauseDialog.mPuzzle = this;
        //pause按钮的点击事件
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pause Click sound effect
                soundPool.play(soundID, 1, 1, 0, 0, 1);

                mTimer.pause();//暂停倒计时
                //暂停游戏
                //弹出pauseDialog
                pauseDialog.show();
            }
        });

        WinDialog winDialog = new WinDialog(this);//游戏胜利对话框
        winDialog.mPuzzle = this;
        //数字按钮点击事件
        for (int i = 1; i < operation.length - 1; i++) {
            int finalI = i;
            operation[i].setOnClickListener(v -> {
                int cellX = puzzleV.mSelectedX;
                int cellY = puzzleV.mSelectedY;
                if (cellX < 0 || cellX > 8 || cellY < 0 || cellY > 8) return;//没有选中拼图时，不做任何操作
                if (operation[finalI].getAlpha() == NOT_FILL_STATUS) return;//按钮与已填充的数据冲突时，不做任何操作

                //保存点击前状态
                key++;
                int[][] noteTemp = new int[9][9];
                boolean[][][] signTemp = new boolean[9][9][9];
                for (int i1 = 0; i1 < 9; i1++) {
                    for (int j1 = 0; j1 < 9; j1++) {
                        System.arraycopy(puzzleV.game.mSign[i1][j1], 0, signTemp[i1][j1], 0, 9);
                        noteTemp[i1][j1] = puzzleV.game.mNotes[i1][j1];
                    }
                }
                revoke.setAlpha(1f);

                noteMap.put(key, noteTemp);
                signMap.put(key, signTemp);
                if (key > keyMax) keyMax = key;

                if (puzzleV.game.mIsSignMode) { //标记模式
                    Log.d(TAG, "标记模式 " + puzzleV.game.mSign[cellX][cellY][finalI - 1]);
                    puzzleV.game.mSign[cellX][cellY][finalI - 1] = !puzzleV.game.mSign[cellX][cellY][finalI - 1];
                } else if (puzzleV.game.mSubject[cellX][cellY] == 0) { //不是题目中的数字
                    if (puzzleV.game.mNotes[cellX][cellY] == finalI) { //点击数字和已有数字相同
                        puzzleV.game.mNotes[cellX][cellY] = 0;
                    } else {    //点击数字和已有数字不同
                        puzzleV.game.mNotes[cellX][cellY] = finalI;//设置为点击数字
                    }
                }

                puzzleV.invalidate();//重绘界面
                //检查是否完成拼图,遍历puzzleV.game.mNotes
                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        if (puzzleV.game.mNotes[x][y] == 0) {
                            puzzleV.frame = 0;
                            puzzleV.game.mIsFinish = false;//设置拼图完成
                            return;
                        }
                    }
                }
                puzzleV.game.mIsFinish = true;//设置拼图完成

                //过关动画.
                ValueAnimator valueAnimator = ValueAnimator.ofInt(0, 255 * 9 - 1);
                valueAnimator.setDuration(2000);
                valueAnimator.setInterpolator(new LinearInterpolator());
                valueAnimator.addUpdateListener(animation -> {
                    puzzleV.frame = (int) animation.getAnimatedValue();
                    puzzleV.invalidate();
                });
                valueAnimator.start();

                //等待动画结束,再弹出胜利对话框
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(() -> winDialog.show());
                }).start();

                Log.d(TAG, "拼图完成");
            });
        }

        ImageView signImage = findViewById(R.id.signImage);//标记按钮
        //sign按钮点击事件
        sign.setOnClickListener(v -> {  //标记模式切换
            if (signImage.getAlpha() == NOT_FILL_STATUS) return;
            if (!puzzleV.game.mIsSignMode) { //当前不是标记模式
                puzzleV.game.mIsSignMode = true;//设置为标记模式
                signImage.setImageResource(R.drawable.ic_play_numberbox_markon);
            } else {
                puzzleV.game.mIsSignMode = false;
                signImage.setImageResource(R.drawable.ic_play_numberbox_markoff);
            }
            puzzleV.setButtonSwitch();
            setButtonColor();
        });

        //revoke按钮点击事件
        revoke.setOnClickListener(v -> {
            if ((key == 0) || (keyMax - key >= revokeMax)) {
                revoke.setAlpha(NOT_FILL_STATUS);
                return;
            }

            //撤销操作
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    puzzleV.game.mNotes[i][j] = Objects.requireNonNull(noteMap.get(key))[i][j];
                    for (int k = 0; k < 9; k++) {
                        puzzleV.game.mSign[i][j][k] = Objects.requireNonNull(signMap.get(key))[i][j][k];
                    }
                }
            }
            puzzleV.invalidate();//重绘界面
            key--;
            if ((key == 0) || (keyMax - key >= revokeMax)) {//撤销到第一步时，或撤销超过20步时，撤销按钮不可用
                revoke.setAlpha(NOT_FILL_STATUS);
            }
        });

        AtomicInteger AuthorChannel = new AtomicInteger();
        Solution solution = new Solution();
        //puzzle_top_text被点击五次
        puzzle_top_text.setOnClickListener(v -> {
            AuthorChannel.getAndIncrement();
            if (AuthorChannel.get() % 5 == 0) {
                Log.d(TAG, "AuthorChannel.get() == 5");
                int[][] answer = solution.solveSudoku(puzzleV.game.mNotes);
                if (answer == null) {
                    Toast.makeText(this, "无解", Toast.LENGTH_SHORT).show();
                } else {
                    //打印结果
                    for (int i = 0; i < 9; ++i) {
                        for (int j = 0; j < 9; ++j) {
                            System.out.print(answer[i][j] + " ");
                        }
                        System.out.println();
                    }
                    for (int x = 0; x < 9; x++) {
                        System.arraycopy(answer[x], 0, puzzleV.game.mNotes[x], 0, 9);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTimer.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTimer.pause();
    }

    void setButtonColor() {
        for (int i = 0; i < operation.length - 1; i++) {
            if (puzzleV.buttonSwitch[i] == 1) {
                if (puzzleV.game.mIsSignMode && i != 0 && i != operation.length - 1) {//标记按钮和撤销按钮不受标记模式切换影响
                    operation[i].setAlpha(SIGN_STATUS);
                } else {
                    operation[i].setAlpha(GAME_STATUS);
                }
            } else {
                operation[i].setAlpha(NOT_FILL_STATUS);
            }
        }
    }
}