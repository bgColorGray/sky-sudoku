package com.example.suduku;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    private int[] line = new int[9];
    private int[] column = new int[9];
    private int[][] block = new int[3][3];
    private boolean valid = false;
    private List<int[]> spaces = new ArrayList<int[]>();

    public int[][] solveSudoku(int[][] board) {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] != 0) { // 如果是非空的，则把该数字放入相应的行、列、九宫格中
                    int digit = board[i][j] - 1;// 因为数组的下标是从0开始的，所以要减1
                    flip(i, j, digit);// 将该数字放入相应的行、列、九宫格中
                }
            }
        }

        while (true) {  // 如果没有空位，则退出循环
            boolean modified = false;
            for (int i = 0; i < 9; ++i) {
                for (int j = 0; j < 9; ++j) {
                    if (board[i][j] == 0) {// 如果是空位，则把该位置的可能的数字放入spaces中
                        int mask = ~(line[i] | column[j] | block[i / 3][j / 3]) & 0x1ff;// 可能的数字的掩码
                        if ((mask & (mask - 1)) == 0) {// 如果可能的数字只有一个，则把该数字放入该位置
                            int digit = Integer.bitCount(mask - 1);// 可能的数字的个数
                            flip(i, j, digit);// 将该数字放入相应的行、列、九宫格中
                            board[i][j] = digit + 1;// 将该数字放入相应的位置
                            modified = true;// 说明有改变
                        }
                    }
                }
            }
            if (!modified) {// 如果没有改变，则退出循环
                break;
            }
        }

        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                if (board[i][j] == 0) {
                    spaces.add(new int[]{i, j});// 如果还有空位，则把该位置的坐标放入spaces中
                }
            }
        }

        dfs(board, 0);// 开始搜索



        if (!valid) {
            throw new IllegalArgumentException("No solution");
        }else{
            return board;
        }
    }

    private void dfs(int[][] board, int pos) {
        if (pos == spaces.size()) {// 如果搜索完了，则说明找到了解
            valid = true;// 说明找到了解
            return;
        }

        int[] space = spaces.get(pos);  // 取出空位的坐标
        int i = space[0], j = space[1]; // 取出空位的行、列
        int mask = ~(line[i] | column[j] | block[i / 3][j / 3]) & 0x1ff;// 可能的数字的掩码
        for (; mask != 0 && !valid; mask &= (mask - 1)) {// 如果掩码不为0，则继续搜索
            int digitMask = mask & (-mask);// 可能的数字的掩码
            int digit = Integer.bitCount(digitMask - 1);// 可能的数字的个数
            flip(i, j, digit);// 将该数字放入相应的行、列、九宫格中
            board[i][j] = digit + 1;// 将该数字放入相应的位置
            dfs(board, pos + 1);// 继续搜索
            flip(i, j, digit);// 将该数字从相应的行、列、九宫格中删除
        }
    }

    private void flip(int i, int j, int digit) { // 将该数字放入相应的行、列、九宫格中
        line[i] ^= (1 << digit);
        column[j] ^= (1 << digit);
        block[i / 3][j / 3] ^= (1 << digit);
    }
}
