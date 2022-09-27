package com.example.suduku.window.map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.suduku.R;
import com.example.suduku.dialog.SettingDialog;
import com.example.suduku.dialog.StartDialog;

public class Map extends AppCompatActivity {

    final int MAX_LEVEL = 40;
    final int ONE = 1;

    //通过SharedPreferences存储游戏进度
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去掉标题栏
        getSupportActionBar().hide();

        setContentView(R.layout.activity_map_to_list_view);

        //通过SharedPreferences存储游戏进度
//        SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);
        //游戏进度从sp中读取
        sp = getSharedPreferences("data", MODE_PRIVATE);
        int levelProgress = sp.getInt("levelProgress", 1);
        Log.d("levelProgress", String.valueOf(levelProgress));


        //滑动地图到当前关卡
        ReboundScrollView rsv = findViewById(R.id.map_scroll);
        if (rsv != null) {
            rsv.post(new Runnable() {
                //ic_map_star_nomal_off高度
                public void run() {
                    rsv.scrollTo(0, MAX_LEVEL * 232 - levelProgress * 232);
                }
                //因为要求界面加载时就是在底部，所以需要新建一个线程来保证
            });
        } else {
            Log.e("MapToListView", "rsv is null");
        }

        //bg_star图片呼吸效果动画
        ImageView bg_star = findViewById(R.id.bg_star);
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(bg_star, "alpha", 0.2f, 0.9f);
        alphaAnimator.setDuration(2500);
        alphaAnimator.setInterpolator(new BraetheInterpolator());
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.start();

        //流星动画,X轴左移，Y轴下移
        ImageView map_flash_big = findViewById(R.id.map_flash_big);
        map_flash_big.setAlpha(0.6f);
        ValueAnimator map_flash_big_animator = ValueAnimator.ofFloat(10000, -200);
        map_flash_big_animator.setDuration(7000);
        map_flash_big_animator.setRepeatCount(ValueAnimator.INFINITE);
        map_flash_big_animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue();
            map_flash_big.setTranslationX(3 * value);
            map_flash_big.setTranslationY(1.4f * -value);
        });
        map_flash_big_animator.start();

        //流星动画,X轴左移，Y轴下移
        ImageView map_flash_small = findViewById(R.id.map_flash_small);
        map_flash_small.setAlpha(0.0f);
        ValueAnimator map_flash_small_animator = ValueAnimator.ofFloat(10000, -250);
        map_flash_small_animator.setStartDelay(3000);
        map_flash_small.setAlpha(0.6f);
        //延时3秒后开始
        map_flash_small_animator.setDuration(7000);
        map_flash_small_animator.setRepeatCount(-1);
        map_flash_small_animator.addUpdateListener(animation -> {
            float value = (float) animation.getAnimatedValue(); //获取当前动画的值
            map_flash_small.setTranslationX(3 * value);//设置X轴移动距离
            map_flash_small.setTranslationY(1.4f * -value);//设置Y轴移动距离
        });
        map_flash_small_animator.start();


        //设置图片ic_map_cover_down和ic_map_cover_top,阴影效果
        ImageView map_cover_down = findViewById(R.id.ic_map_cover_down);
        map_cover_down.setZ(1);
        ImageView map_cover_top = findViewById(R.id.ic_map_cover_top);
        map_cover_top.setZ(1);

        //setting按钮不会被覆盖
        ImageView setting = findViewById(R.id.setting);
        setting.setZ(2);

        SettingDialog settingDialog = new SettingDialog(this);
        //setting点击事件,弹出settingDialog
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //打印提示
                System.out.println("setting点击事件");
                //弹出settingDialog
                settingDialog.show();

            }
        });

        //加载界面
        androidx.constraintlayout.widget.ConstraintLayout map_container = findViewById(R.id.map_container);
        ConstraintSet constraintSet = new ConstraintSet();
        //加载关卡
        for (int i = 1; i <= MAX_LEVEL; i++) {
            NoteStar noteStar = new NoteStar();
            noteStar.starImage = new ImageView(this);
            noteStar.starText = new TextView(this);
            noteStar.dottedLineImage = new ImageView(this);

            //加入布局
            map_container.addView(noteStar.starImage);
            map_container.addView(noteStar.starText);
            map_container.addView(noteStar.dottedLineImage);

            //设置节点id
            noteStar.starImage.setId(2000 + i);
            noteStar.starText.setId(3000 + i);
            noteStar.dottedLineImage.setId(4000 + i);

            //设置节点共用参数
            noteStar.starText.setTextSize(25);
            noteStar.starText.setText(String.valueOf(i));
            noteStar.starText.setTextColor(0xFFFFFFFF);
            if (i < levelProgress) {//已通过关卡
                noteStar.starImage.setImageResource(R.drawable.ic_map_star_nomal_on);
                //黄色
                noteStar.starText.setTextColor(0xFFFFC107);

            } else if (i == levelProgress) {//可玩最新关卡
                noteStar.starImage.setImageResource(R.drawable.ic_map_star_light);

                //starImage呼吸动画
                ObjectAnimator al = ObjectAnimator.ofFloat(noteStar.starImage, "alpha", 0.6f, 1f);
                al.setDuration(2500);
                al.setInterpolator(new BraetheInterpolator());
                al.setRepeatCount(ValueAnimator.INFINITE);
                al.start();

            } else {//不可玩关卡
                noteStar.starImage.setImageResource(R.drawable.ic_map_star_nomal_off);
            }

            constraintSet.clone(map_container);
            //约束starText的位置，在starImage上方50dp
            constraintSet.connect(noteStar.starText.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.BOTTOM, 110);
            constraintSet.connect(noteStar.starText.getId(), ConstraintSet.LEFT, noteStar.starImage.getId(), ConstraintSet.LEFT, 0);
            constraintSet.connect(noteStar.starText.getId(), ConstraintSet.RIGHT, noteStar.starImage.getId(), ConstraintSet.RIGHT, 0);

            switch (i % 4) {
                case 0:
                    //约束starImage的位置
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId() - ONE, ConstraintSet.TOP, 100);
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.LEFT, noteStar.starImage.getId() - ONE, ConstraintSet.RIGHT, 100);

                    //约束dottedLineImage的位置
                    if (i == MAX_LEVEL) {  //最后一个关卡不画线
                        //上方添加空白节点
                        noteStar.dottedLineImage.setImageResource(R.drawable.ic_htp_grid_bg1);
                        constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.TOP, 200);
                        break;
                    }
                    noteStar.dottedLineImage.setImageResource(R.drawable.ic_map_line_left_off);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.RIGHT, noteStar.starImage.getId(), ConstraintSet.LEFT, 0);
                    break;
                case 1:
                    //约束dottedLineImage的位置
                    noteStar.dottedLineImage.setImageResource(R.drawable.ic_map_line_left_off);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.RIGHT, noteStar.starImage.getId(), ConstraintSet.LEFT, 0);

                    if (i == 1) {   //手动添加第一个关卡的约束
                        if (levelProgress == 1) {
                            noteStar.starImage.setImageResource(R.drawable.ic_map_star_small_off);
                        } else {
                            noteStar.starImage.setImageResource(R.drawable.ic_map_star_small_on);
                        }
                        //约束starImage的位置，在map_container底部中间
                        constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.BOTTOM, map_container.getId(), ConstraintSet.BOTTOM, 300);
                        constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.LEFT, map_container.getId(), ConstraintSet.LEFT, 0);
                        constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.RIGHT, map_container.getId(), ConstraintSet.RIGHT, 0);
                        break;
                    }
                    //约束starImage的位置
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId() - ONE, ConstraintSet.TOP, 100);
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.RIGHT, noteStar.starImage.getId() - ONE, ConstraintSet.LEFT, 100);

                    break;

                case 2:
                    //约束starImage的位置
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId() - ONE, ConstraintSet.TOP, 100);
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.RIGHT, noteStar.starImage.getId() - ONE, ConstraintSet.LEFT, 100);

                    //约束dottedLineImage的位置
                    noteStar.dottedLineImage.setImageResource(R.drawable.ic_map_line_right_off);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.LEFT, noteStar.starImage.getId(), ConstraintSet.RIGHT, 0);
                    break;
                case 3:
                    //约束starImage的位置
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId() - ONE, ConstraintSet.TOP, 100);
                    constraintSet.connect(noteStar.starImage.getId(), ConstraintSet.LEFT, noteStar.starImage.getId() - ONE, ConstraintSet.RIGHT, 100);

                    //约束dottedLineImage的位置
                    noteStar.dottedLineImage.setImageResource(R.drawable.ic_map_line_right_off);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.BOTTOM, noteStar.starImage.getId(), ConstraintSet.TOP, 0);
                    constraintSet.connect(noteStar.dottedLineImage.getId(), ConstraintSet.LEFT, noteStar.starImage.getId(), ConstraintSet.RIGHT, 0);
                    break;
            }
            constraintSet.applyTo(map_container);   //应用约束

            //starImage点击事件
            StartDialog startDialog = new StartDialog(this);
            int finalI = i;
            noteStar.starImage.setOnClickListener(v -> {
                if (finalI <= levelProgress) {
                    startDialog.show();//弹出确认界面
                    startDialog.setLevel(finalI);//设置关卡等级文本
                }
                if (finalI < levelProgress) {
                    startDialog.setImage();
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        int levelProgress = sp.getInt("levelProgress", 1);
        Log.d("levelProgress", String.valueOf(levelProgress));
        //滑动地图到当前关卡
        ReboundScrollView rsv = findViewById(R.id.map_scroll);
        if (rsv != null) {
            rsv.post(new Runnable() {
                //ic_map_star_nomal_off高度
                public void run() {
                    rsv.scrollTo(0, MAX_LEVEL * 232 - levelProgress * 232);
                }
                //因为要求界面加载时就是在底部，所以需要新建一个线程来保证
            });
        } else {
            Log.e("MapToListView", "rsv is null");
        }

        //更新星星状态
        for (int i = 1; i < levelProgress; i++) {
            NoteStar noteStar = new NoteStar();
            noteStar.starImage = findViewById(2000 + i);
            noteStar.starText = findViewById(3000 + i);
            noteStar.starImage.setImageResource(R.drawable.ic_map_star_nomal_on);
            //黄色
            noteStar.starText.setTextColor(0xFFFFC107);
        }
        NoteStar noteStar = new NoteStar();
        noteStar.starImage = findViewById(2000 + levelProgress);
        noteStar.starText = findViewById(3000 + levelProgress);
        noteStar.starImage.setImageResource(R.drawable.ic_map_star_light);
        ObjectAnimator al = ObjectAnimator.ofFloat(noteStar.starImage, "alpha", 0.6f, 1f);
        al.setDuration(2500);
        al.setInterpolator(new BraetheInterpolator());
        al.setRepeatCount(ValueAnimator.INFINITE);
        al.start();
    }

    //关卡节点类
    class NoteStar {
        ImageView starImage;
        TextView starText;
        ImageView dottedLineImage;
    }
}