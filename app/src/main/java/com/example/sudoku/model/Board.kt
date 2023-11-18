package com.example.sudoku.model

import android.media.SoundPool
import android.os.Build.VERSION_CODES.S
import android.os.SystemClock
import android.widget.Button
import android.widget.Toast
import com.example.sudoku.R
import com.example.sudoku.databinding.ActivityMainBinding
import kotlin.random.Random

class Board {
    var mistakeCount = 0

    lateinit var binding: ActivityMainBinding
    var terminateFlag = false
    var originBoard = Array(9) { IntArray(9) { 0 } }
    var board = Array(9) { IntArray(9) { 0 } }
    var row = Array(10) { IntArray(10) { 0 } }
    var col = Array(10) { IntArray(10) { 0 } }
    var diag = Array(10) { IntArray(10) { 0 } }
    var modifyStatusA = Array(9) { IntArray(9) { 0 } }
    var modifyFlag = 0
    var modifiedButton = arrayListOf<Button>()
    var mRow = -1
    var mCol = -1
    fun numberButton(selectedButton: Button, row: Int, col: Int, number: Int) {
        selectedButton.setOnClickListener {
            selectedButton.text = "${number}"
            board[row][col] = number
            if (originBoard[row][col] != number) {
                mistakeCount += 1
                binding.mistake.text = "${mistakeCount}"
                selectedButton.setTextColor(resources.getColor(R.color.red))
                Toast.makeText(this, "${mistakeCount}회 실수", Toast.LENGTH_SHORT).show()
            } else {
                selectedButton.setTextColor(resources.getColor(R.color.purple_500))
            }
            blockColor(selectedButton, row, col)
            if (modifyFlag == 1) {
                modifyFlag = 0
                modifiedButton.remove(modifiedButton[0])
            }
        }
    }

    fun makeBoard(k: Int): Boolean {

        if (terminateFlag) {
            return true
        }

        if (k > 80) {
            for (i in 0 until 9) {
                for (j in 0 until 9) {
                    board[i][j] = originBoard[i][j]
                }
            }
            terminateFlag = true
            return true
        }

        val (i, j) = k / 9 to k % 9
        val startNum = Random.nextInt(1, 10)

        if (originBoard[i][j] != 0) {
            makeBoard(k + 1)
        }

        for (m in 1..9) {
            val num = 1 + (m + startNum) % 9
            val d = (i / 3) * 3 + (j / 3)
            if (row[i][num] == 0 && col[j][num] == 0 && diag[d][num] == 0) {
                row[i][num] = 1
                col[j][num] = 1
                diag[d][num] = 1
                originBoard[i][j] = num
                makeBoard(k + 1)
                row[i][num] = 0
                col[j][num] = 0
                diag[d][num] = 0
                originBoard[i][j] = 0
            }
        }
        return false
    }

    fun boardInitialize() {

        binding.clock.base = SystemClock.elapsedRealtime()
        binding.clock.start()
        // 시간 시작

        originBoard = Array(9) { IntArray(9) { 0 } }
        board = Array(9) { IntArray(9) { 0 } }
        row = Array(10) { IntArray(10) { 0 } }
        col = Array(10) { IntArray(10) { 0 } }
        diag = Array(10) { IntArray(10) { 0 } }
        modifyStatusA = Array(9) { IntArray(9) { 0 } }
        modifyFlag = 0
        modifiedButton = arrayListOf<Button>()
        mRow = -1
        mCol = -1
        terminateFlag = false
        mistakeCount = 0
        binding.mistake.text = "0"
        // 변수 초기화
        val seqDiag = listOf(0, 4, 8)
        for (offset in 0 until 9 step 3) {
            val seq = (1..9).toMutableList().apply { shuffle() }
            for (idx in 0 until 9) {
                val (i, j) = idx / 3 to idx % 3
                row[offset + i][seq[idx]] = 1
                col[offset + j][seq[idx]] = 1
                val k = seqDiag[offset / 3]
                diag[k][seq[idx]] = 1
                originBoard[offset + i][offset + j] = seq[idx]
            }
        }
        // 보드판 초기화

        makeBoard(0)
        // 보드 만드는 함수 호출
        for (i in 0..8) {
            for (j in 0..8) {
                originBoard[i][j] = board[i][j]
                modifyStatusA[i][j] = 1
            }
        }

        var temp = mutableSetOf<Int>()
        while (temp.size < 51) {
            temp.add(Random.nextInt(9) * 10 + Random.nextInt(9))
        }
        for (i in temp) {
            board[i / 10][i % 10] = 0
        }
        // 무작위 51개 비우기
        fun blockColor(selectedButton: Button, row: Int, col: Int) {
            if (row <= 2 && col <= 2) {
                selectedButton.setBackgroundResource(R.drawable.first_box)
            } else if (row <= 2 && col >= 6) {
                selectedButton.setBackgroundResource(R.drawable.first_box)
            } else if (row >= 3 && col >= 3 && row <= 5 && col <= 5) {
                selectedButton.setBackgroundResource(R.drawable.first_box)
            } else if (row >= 6 && col <= 2) {
                selectedButton.setBackgroundResource(R.drawable.first_box)
            } else if (row >= 6 && col >= 6) {
                selectedButton.setBackgroundResource(R.drawable.first_box)
            } else {
                selectedButton.setBackgroundResource(R.drawable.second_box)
            }
        }


        fun keys(selectedButton: Button, row: Int, col: Int) {
            selectedButton.setOnClickListener {
                MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                if (modifyStatusA[row][col] == 1) {
                    if (modifyFlag == 0) {
                        selectedButton.setBackgroundResource(R.drawable.selected_box)
                        modifiedButton.add(selectedButton)
                        mRow = row
                        mCol = col
                        modifyFlag = 1
                    } else {
                        blockColor(modifiedButton[0], mRow, mCol)
                        selectedButton.setBackgroundResource(R.drawable.selected_box)
                        modifiedButton.remove(modifiedButton[0])
                        modifiedButton.add(selectedButton)
                        mRow = row
                        mCol = col
                        modifyFlag = 1
                    }
                    binding.one.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 1)
                    }
                    binding.two.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 2)
                    }
                    binding.three.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 3)
                    }
                    binding.four.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 4)
                    }
                    binding.five.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 5)
                    }
                    binding.six.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 6)
                    }
                    binding.seven.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 7)
                    }
                    binding.eight.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 8)
                    }
                    binding.nine.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectClockSound(Sounds().clock)
                        numberButton(selectedButton, row, col, 9)
                    }
                    binding.deleteB.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectSound(Sounds().sound3)
                        selectedButton.text = ""
                        board[row][col] = 0
                        blockColor(selectedButton, row, col)
                        if (modifyFlag == 1) {
                            modifyFlag = 0
                            modifiedButton.remove(modifiedButton[0])
                        }
                    }
                    binding.hintB.setOnClickListener {
                        MusicPlayer(Sounds().soundPool).playEffectSound(Sounds().sound4)
                        selectedButton.text = "${originBoard[row][col]}"
                        board[row][col] = originBoard[row][col]
                        selectedButton.setTextColor(resources.getColor(R.color.purple_500))
                        blockColor(selectedButton, row, col)
                        if (modifyFlag == 1) {
                            modifyFlag = 0
                            modifiedButton.remove(modifiedButton[0])
                        }
                    }
                }
            }

        }


        var boardButtons = Buttons().returnButton()

        for (b in boardButtons) {
            b.setTextAppearance(R.style.myTitleText)
        }


        for (i in 0..8) {
            for (j in 0..8) {
                if (board[i][j] == 0) {
                    boardButtons[i * 9 + j].isEnabled = true
                } else {
                    boardButtons[i * 9 + j].isEnabled = false
                }
            }
        }

        for (i in 0..8) {
            for (j in 0..8) {
                boardButtons[i * 9 + j].text = if (board[i][j] == 0) "" else "${board[i][j]}"
                if (board[i][j] == 0) keys(selectedButton = boardButtons[i * 9 + j], i, j)
                blockColor(boardButtons[i * 9 + j], i, j)
            }
        }


    }
}