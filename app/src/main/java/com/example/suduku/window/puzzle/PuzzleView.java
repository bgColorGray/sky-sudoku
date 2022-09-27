package com.example.suduku.window.puzzle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Arrays;

public class PuzzleView extends View {
    private static final String TAG = "PuzzleView";

    public static float TEXT_IN_CELL = 1.4f * 0.55f;//文字占单元格大小的比例，设置为1.4时，文字大小为单元格大小
    public static float TEXT_IN_CELL_CENTER_MOVE_X = 0.5f;//文字居中时，X方向移动的距离。在设置文字画笔setTextAlign(Paint.Align.CENTER)时，X为文字中部坐标，X方向移动的距离是cell的一半。
    private static final Float TEXT_IN_CELL_CENTER_MOVE_Y = (1f - TEXT_IN_CELL / 1.4f) / 2f;//文字居中时，Y方向移动的距离,Y为文字底部坐标。向上移，为负值。为cell减去文字高度的一半。

    private static final Float SIGN_TEXT_IN_CELL = TEXT_IN_CELL / 3f;//标记文字大小
    private static final Float SIGN_TEXT_IN_CELL_CENTER_MOVE_X = 1f / 6f;//标记文字在小格子内X方向移动的距离，147->(X+0f/3f)cell，258 -> (X+1f/3f)cell，369-> (X+2f/3f)cell。
    private static final Float SIGN_TEXT_IN_CELL_CENTER_MOVE_Y = (1f / 3f - SIGN_TEXT_IN_CELL / 1.4f) / 2f;//标记文字X方向移动的距离倍数，789一倍，456三倍，123五倍。

    public int frame = 0;

    public PuzzleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Game game = new Game();
    public int level = 0;//游戏难度等级
    public int buttonSwitch[] = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};//操作面板按钮开关,0为关闭,1为开启.sign 1-9 revoke
    protected float startBoxX = 0f;    // 开始的横坐标
    protected float boxWidth = 0f;     // 方框宽度
    protected float boxHeight = 0f;    // 方框高度
    protected float cellWidth = 0f;    // 单元格宽度
    protected float cellHeight = 0f;   // 单元格高度
    protected float padWidth = 0f;     // 单元格内间隔宽度
    protected float padHeight = 0f;    // 单元格内间隔高度
    protected float diqitX = 0f;       // 单元格内数字的X坐标
    protected float diqitY = 0f;       // 单元格内数字的Y坐标
    protected float lineStroke = 0f;   // 网格线的宽度
    protected float boxStroke = 0f;    // 格子的宽度

    protected Paint backgroundPaint = new Paint();//背景画笔
    protected Paint givensPaint = new Paint();    //数字画笔
    protected Paint linePaint = new Paint();      //网格线画笔
    protected Paint signPaint = new Paint();      //标记画笔
    Path path = new Path();

    public int mSelectedX = -1;
    public int mSelectedY = -1;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        game.init(level);

        boxWidth = w * 0.90f;    // 方框宽度
        boxHeight = boxWidth;    // 方框高度
        cellWidth = boxWidth / 9f;  // 单元格宽度
        cellHeight = boxHeight / 9f;    // 单元格高度

        padWidth = (w % 9) / 2f;// 单元格内间隔宽度
        padHeight = (h % 9) / 2f;
        Paint.FontMetrics fontMetrics = givensPaint.getFontMetrics();
        diqitX = cellWidth / 2f;
        diqitY = cellHeight / 2f - (fontMetrics.ascent + fontMetrics.descent) * 2f;
        lineStroke = cellWidth / 36f;
        boxStroke = cellWidth / 12f;

        startBoxX = (w - (3 * boxStroke + 9 * cellWidth + 6 * lineStroke)) / 2; // 开始的横坐标

        givensPaint.setColor(Color.BLACK);
        givensPaint.setStyle(Paint.Style.FILL);  // 画笔填充模式
        givensPaint.setTypeface(Typeface.DEFAULT_BOLD);
        givensPaint.setTextSize(cellHeight * TEXT_IN_CELL);//数字字体大小与单元格高度相关
        givensPaint.setTextAlign(Paint.Align.CENTER);//设置文字居中对齐
        givensPaint.setAntiAlias(true);        //抗锯齿


        signPaint.setColor(Color.GRAY);
        signPaint.setStyle(Paint.Style.FILL);  // 画笔填充模式
        signPaint.setTypeface(Typeface.DEFAULT_BOLD);
        signPaint.setTextSize(cellHeight * SIGN_TEXT_IN_CELL);//标记数字大小
        signPaint.setTextAlign(Paint.Align.CENTER);//设置文字居中对齐

        linePaint.setColor(Color.BLACK);
        linePaint.setStrokeWidth(lineStroke);
    }

    @Override
    protected void onDraw(android.graphics.Canvas canvas) {
        super.onDraw(canvas);

        //在box周围画白色边框
        backgroundPaint.setColor(0xFFFFFFFF);//白色
        canvas.drawRect(startBoxX - 2 * boxStroke, startBoxX - 2 * boxStroke, getWidth() - startBoxX + 2 * boxStroke, getWidth() - startBoxX + 2 * boxStroke, backgroundPaint);

        //画数字背景
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                int note = game.mNotes[i][j];

                float cellLeft = startBoxX + (i + 2) / 3 * boxStroke + ((i % 3 == 0) ? 0.5f : 0) * boxStroke + i * cellWidth + (i - ((i + 2) / 3)) * lineStroke + ((i % 3 == 0) ? 0 : 0.5f) * lineStroke;
                float cellTop = startBoxX + (j + 2) / 3 * boxStroke + ((j % 3 == 0) ? 0.5f : 0) * boxStroke + j * cellHeight + (j - ((j + 2) / 3)) * lineStroke + ((j % 3 == 0) ? 0 : 0.5f) * lineStroke;
                float cellRight = cellLeft + cellWidth + lineStroke;
                float cellBottom = cellTop + cellWidth;

                if (mSelectedX >= 0 && mSelectedY >= 0 && mSelectedX <= 8 && mSelectedY <= 8) {  //有效单元格，初始不画
                    backgroundPaint.setColor(0xFFEFEF35);   //黄色


                    if (game.mNotes[mSelectedX][mSelectedY] == 0) {//点击到没有数字的单元格
                        int boxLeftTopX = mSelectedX / 3 * 3;
                        int boxLeftTopY = mSelectedY / 3 * 3;

                        //打印mSelectedX和mSelectedY的值
                        if (mSelectedX == i || mSelectedY == j ||
                                ((i >= boxLeftTopX && i <= boxLeftTopX + 2) &&
                                        (j >= boxLeftTopY && j <= boxLeftTopY + 2))
                        ) {//如果是选中的单元格所在行或列，并且该单元格没有数字，则画一个黄色的背景
                            if (mSelectedX == i && mSelectedY == j) {   //如果是选中的单元格，则不画
                                canvas.drawLine(cellLeft, cellTop, cellLeft + cellWidth, cellTop, linePaint);
                                canvas.drawLine(cellLeft, cellTop, cellLeft, cellTop + cellHeight, linePaint);
                                canvas.drawLine(cellLeft, cellTop + cellHeight, cellLeft + cellWidth, cellTop + cellHeight, linePaint);
                                canvas.drawLine(cellLeft + cellWidth, cellTop, cellLeft + cellWidth, cellTop + cellHeight, linePaint);
                            } else {
                                canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, backgroundPaint);//画数字背景
                            }
                        }
                    } else if (game.mSubject[mSelectedX][mSelectedY] == 0) {  //点击到填充的单元格
                        if (note == game.mNotes[mSelectedX][mSelectedY]) {
                            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, backgroundPaint);//画数字背景
                        }
                        if (i == mSelectedX && j == mSelectedY) {
                            //设置画笔为深黄色
                            int color = backgroundPaint.getColor();
                            backgroundPaint.setColor(0xFFFF7700);
                            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, backgroundPaint);//画数字背景
                            backgroundPaint.setColor(color);//恢复
                        }
                    } else {    //点击原有数字单元格
                        if (note == game.mSubject[mSelectedX][mSelectedY]) {
                            canvas.drawRect(cellLeft, cellTop, cellRight, cellBottom, backgroundPaint);//画数字背景
                        }
                    }
                }

                if (note == 0) {//没有数字则画标记
                    for (int k = 0; k < 9; k++) {
                        if (game.mSign[i][j][k]) {  //如果有标记
                            canvas.drawText(String.valueOf(k + 1), cellLeft + (((k % 3) / 3f) + SIGN_TEXT_IN_CELL_CENTER_MOVE_X) * cellWidth, cellBottom - (((2 - (k / 3)) / 3f) + SIGN_TEXT_IN_CELL_CENTER_MOVE_Y) * cellWidth, signPaint);//画数字
                        }
                    }
                } else {//画数字，数字闪烁动画
                    if (game.mSubject[i][j] == 0) {//判断是否为题目的数字
                        givensPaint.setColor(0xFF000000);//黑色
                    } else {
                        //灰色
                        givensPaint.setColor(Color.GRAY);
                        //cell左上角画实心三角形
                        path.moveTo(cellLeft, cellTop);
                        path.lineTo(cellLeft + cellWidth / 8f, cellTop);
                        path.lineTo(cellLeft, cellTop + cellHeight / 8f);
                        canvas.drawPath(path, linePaint);
                        path.reset();
                    }
                    if (game.mIsFinish) {//过关动画
                        if (j % 2 == 0 && ((8 - j) == frame / 255)) {
                            int color = (((frame % 255) / 2 + 128) << 24);
                            givensPaint.setColor(color);
                            TEXT_IN_CELL_CENTER_MOVE_X = 0.5f + (i - 4f) * 0.05f * ((frame % 255 / 128f) < 1 ? (frame % 255 / 128f) : (2 - (frame % 255 / 128f)));
                        } else if (j % 2 == 1 && ((8 - j) == (frame + 30) / 255)) {
                            int tempFrame = frame + 30;
                            int color = (((tempFrame % 255) / 2 + 128) << 24);
                            givensPaint.setColor(color);
                            TEXT_IN_CELL_CENTER_MOVE_X = 0.5f + (i - 4f) * 0.05f * ((tempFrame % 255 / 128f) < 1 ? (tempFrame % 255 / 128f) : (2 - (tempFrame % 255 / 128f)));
                        }
                    }
                    canvas.drawText(String.valueOf(note), cellLeft + TEXT_IN_CELL_CENTER_MOVE_X * cellWidth, cellBottom - TEXT_IN_CELL_CENTER_MOVE_Y * cellWidth, givensPaint);//画数字
                    TEXT_IN_CELL_CENTER_MOVE_X = 0.5f;
                }
            }
        }

        //画网格
        linePaint.setColor(0xFF000000);//网格线颜色为黑色
        for (int i = 0; i <= 9; i++) {
            if (i % 3 == 0) {
                linePaint.setStrokeWidth(boxStroke);    //画粗线
            } else {
                linePaint.setStrokeWidth(lineStroke);   //画细线
            }

            float startX = startBoxX - 0.5f * boxStroke;
            float startY = startBoxX + (i + 2) / 3 * boxStroke + i * cellWidth + (i - ((i + 2) / 3)) * lineStroke;
            float stopX = startBoxX + (9 + 2) / 3 * boxStroke + 9 * cellWidth + (9 - ((9 + 2) / 3)) * lineStroke + 0.5f * boxStroke;
            float stopY = startBoxX + (i + 2) / 3 * boxStroke + i * cellWidth + (i - ((i + 2) / 3)) * lineStroke;

            canvas.drawLine(startX, startY, stopX, stopY, linePaint);//画横线
            canvas.drawLine(startY, startX, stopY, stopX, linePaint);//画竖线
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {   //触摸事件
        if (event.getAction() != android.view.MotionEvent.ACTION_DOWN) {//
            return super.onTouchEvent(event);
        }

        mSelectedX = (int) ((event.getX() - startBoxX) / (cellWidth + 0.5f / 9 * cellWidth));
        mSelectedY = (int) ((event.getY() - startBoxX) / (cellWidth + 0.5f / 9 * cellWidth));

        if (mSelectedX < 0 || mSelectedX > 8 || mSelectedY < 0 || mSelectedY > 8) {  //在box内并且不是题目格子
            return super.onTouchEvent(event);
        }

        setButtonSwitch();

        //打印触摸的坐标不可以填入的数字
        Log.d(TAG, "onTouchEvent: " + mSelectedX + " " + mSelectedY + Arrays.toString(game.getPossibleNotes(mSelectedX, mSelectedY)));

        invalidate();//重绘
        return true;
    }

    void setButtonSwitch() {
        Arrays.fill(buttonSwitch, 1);//初始化buttonSwitch数组

        if (!game.mIsSignMode) {//标记模式按钮都可点击
            if (mSelectedX >= 0 && mSelectedX <= 8 && mSelectedY >= 0 && mSelectedY <= 8) {
                int[] blackButton = game.getPossibleNotes(mSelectedX, mSelectedY);//获取黑色数字按钮数组
                for (int j : blackButton) {//遍历设置黑色数字按钮
                    buttonSwitch[j] = 0;
                }
            }
        }

        //如果该格子有数字，sign不可点击，否则可点击
        if (mSelectedX >= 0 && mSelectedX <= 8 && mSelectedY >= 0 && mSelectedY <= 8) {
            if (game.mNotes[mSelectedX][mSelectedY] != 0) {
                buttonSwitch[0] = 0;
            }
        }
    }
}
