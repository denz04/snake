package com.fun4.snake;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Random;
import android.content.res.AssetManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


class SnakeEngine extends SurfaceView implements Runnable  {

    // Our game thread for the main game loop
    private Thread thread = null;
    private float x1,x2,y1,y2;
    static final int MIN_DISTANCE = 70;

    // To hold a reference to the Activity
    private Context context;
    //private GestureDetectorCompat mDetector;
    // for plaing sound effects
    private SoundPool soundPool;
    private int eat_bob = -1;
    private int snake_crash = -1;

    // For tracking movement Heading
    public enum Heading {UP, RIGHT, DOWN, LEFT}
    // Start by heading to the right
    private Heading heading = Heading.RIGHT;

    // To hold the screen size in pixels
    private int screenX;
    private int screenY;

    // How long is the snake
    private int snakeLength;

    // Where is Bob hiding?
    private int bobX;
    private int bobY;
    private int dbobX;
    private int dbobY;

    // The size in pixels of a snake segment
    private int blockSize;

    // The size in segments of the playable area
    private final int NUM_BLOCKS_WIDE = 40;
    private int numBlocksHigh;

    // Control pausing between updates
    private long nextFrameTime;
    // Update the game 10 times per second
    private final long FPS = 10;
    // There are 1000 milliseconds in a second
    private final long MILLIS_PER_SECOND = 1000;
// We will draw the frame much more often

    // How many points does the player have
    private int score;

    // The location in the grid of all the segments
    private int[] snakeXs;
    private int[] snakeYs;

    // Everything we need for drawing
// Is the game currently playing?
    private volatile boolean isPlaying;

    // A canvas for our paint
    private Canvas canvas;

    // Required to use canvas
    private SurfaceHolder surfaceHolder;

    // Some paint for our canvas
    private Paint paint;
    private Paint dPaint;
    private int y11 ;

    private int x11 ;
    private int h1,h2,h3,h4,h5,h6,h7,h8,h9,h10,h11,h12,h13,h14;
    private int a1,a2,a3,a4,a5,a6,a7,a8,a9,a10;
    private boolean isUp = false;
    private boolean isRight = false;
    private boolean isLefft = false;
    private boolean isDown = false;
    public SnakeEngine(Context context, Point size) {
        super(context);

        context = context;

        screenX = size.x;
        screenY = size.y;
        x11 = screenX;
        y11 = screenY;

        // Work out how many pixels each block is
        blockSize = screenX / NUM_BLOCKS_WIDE;
        // How many blocks of the same size will fit into the height
        numBlocksHigh = screenY / blockSize;

        // Set the sound up
        soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        try {
            // Create objects of the 2 required classes
            // Use m_Context because this is a reference to the Activity
            AssetManager assetManager = context.getAssets();
            AssetFileDescriptor descriptor;

            // Prepare the two sounds in memory
            descriptor = assetManager.openFd("get_mouse_sound.ogg");
            eat_bob = soundPool.load(descriptor, 0);

            descriptor = assetManager.openFd("death_sound.ogg");
            snake_crash = soundPool.load(descriptor, 0);

        } catch (IOException e) {
            // Error
        }


        // Initialize the drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();
        dPaint = new Paint();

        // If you score 200 you are rewarded with a crash achievement!
        snakeXs = new int[200];
        snakeYs = new int[200];

        // Start the game
        newGame();
    }

    @Override
    public void run() {

        while (isPlaying) {

            // Update 10 times a second
            if(updateRequired()) {
                update();
                draw();
            }


        }
    }

    public void pause() {
        isPlaying = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            // Error
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void newGame() {
        // Start with a single snake segment
        snakeLength = 1;
        snakeXs[0] = NUM_BLOCKS_WIDE / 2;
        snakeYs[0] = numBlocksHigh / 2;
        h1 = 0;
        h2 = 0;
        h3 = 0;
        h4 = 0;
        h5 = 0;
        h6 = 0;
        h7 = 0;
        h8 = 0;
        h9 = 0;
        h10 = 0;
        h11 = 0;
        h12 = 0;
        h13 = 0;
        h14 = 0;
        a1 = 0;
        a2 = 0;
        a3 = 0;
        a4 = 0;
        a5 = 0;
        a6 = 0;
        a7 = 0;
        a8 = 0;
        a9 = 0;
        a10 = 0;
        isUp = false;
        isRight = false;
        isLefft = false;
        isDown = false;
        // Get Bob ready for dinner
        spawnBob();

        // Reset the score
        score = 0;

        // Setup nextFrameTime so an update is triggered
        nextFrameTime = System.currentTimeMillis();
    }


    public void spawnBob() {
        //Random random = new Random();
        /*bobX = blockSize/2;
        bobY = numBlocksHigh/2;
        int letter_completed = 0;*/
        dbobX = bobX;
        dbobY = bobY;
        Log.i("h1 : ", ""+h1);
        Log.i("h2 : ", ""+h2);
        Log.i("h3 : ", ""+h3);
        if(h1 == 0){
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            h1 = 1;
        }else if(h2 == 0){
            bobX = blockSize/2;
            bobY = numBlocksHigh/2-1;
            h2 = 1;
        }else if(h3 == 0){
            bobX = blockSize/2;
            bobY = numBlocksHigh/2-2;
            h3 = 1;
        }else if(h4 == 0){
            bobX = blockSize/2;
            bobY = numBlocksHigh/2-3;
            h4 = 1;
        }else if(h5 == 0){
            bobX = blockSize/2;
            bobY = numBlocksHigh/2-4;
            h5 = 1;
        }else if(h6 == 0){
            bobX = blockSize/2+1;
            bobY = numBlocksHigh/2-3;
            h6 = 1;
        }else if(h7 == 0){
            bobX = blockSize/2+2;
            bobY = numBlocksHigh/2-3;
            h7 = 1;
        }else if(h8 == 0) {
            bobX = blockSize/2+3;
            bobY = numBlocksHigh/2-3;
            h8 = 1;
        }else if(h9 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2-3;
            h9 = 1;
        }else if(h10 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2-4;
            h10 = 1;
        }else if(h11 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2-3;
            h11 = 1;
        }else if(h12 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2-2;
            h12 = 1;
        }else if(h13 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2-1;
            h13 = 1;
        }else if(h14 == 0) {
            bobX = blockSize/2+4;
            bobY = numBlocksHigh/2;
            h14 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }else if(a1 == 0) {
            bobX = blockSize/2;
            bobY = numBlocksHigh/2;
            a1 = 1;
        }
        Log.i("position x,y :",""+bobX+" "+bobY);
       /* bobX = x11/2;
        bobY = y11/2;*/

    }

    private void eatBob(){
        //  Got him!
        // Increase the size of the snake
        snakeLength++;
        //replace Bob
        // This reminds me of Edge of Tomorrow. Oneday Bob will be ready!
        spawnBob();
        //add to the score
        score = score + 1;
        soundPool.play(eat_bob, 1, 1, 0, 0, 1);
    }

    private void moveSnake(){
        // Move the body
        int done = 0;
        for (int i = snakeLength; i > 0; i--) {
            // Start at the back and move it
            // to the position of the segment in front of it
            snakeXs[i] = snakeXs[i - 1];
            snakeYs[i] = snakeYs[i - 1];
            if(snakeXs[0] >= NUM_BLOCKS_WIDE && done == 0) {
                snakeXs[0] = 0;
                done = 1;
            }else if (snakeYs[0] >= numBlocksHigh && done == 0) {
                snakeYs[0] = 0;
                done = 1;
            }else if(snakeXs[0] <= 0 && done == 0) {
                snakeXs[0] = NUM_BLOCKS_WIDE;
                done = 1;
            }else if (snakeYs[0] <= 0 && done == 0) {
                snakeYs[0] = numBlocksHigh;
                done = 1;
            }

            // Exclude the head because
            // the head has nothing in front of it
        }

        // Move the head in the appropriate heading
        switch (heading) {
            case UP:
                isUp = true;
                isRight = false;
                isLefft = false;
                isDown = false;
                snakeYs[0]--;
                break;

            case RIGHT:
                isUp =false;
                isRight = true;
                isLefft = false;
                isDown = false;
                snakeXs[0]++;
                break;

            case DOWN:
                isUp = false;
                isRight = false;
                isLefft = false;
                isDown = true;
                snakeYs[0]++;
                break;

            case LEFT:
                isUp = false;
                isRight = false;
                isLefft = true;
                isDown = false;
                snakeXs[0]--;
                break;
        }
        //Log.i("heading on :","dir"+isUp+isDown+isRight+isLefft);
        //Log.i("heading on :","dir"+isUp+isDown+isRight+isLefft);
    }

    private boolean detectDeath(){
        // Has the snake died?
        boolean dead = false;

        // Hit the screen edge
        if (snakeXs[0] == -1) dead = true;
        if (snakeXs[0] >= NUM_BLOCKS_WIDE) dead = true;
        if (snakeYs[0] == -1) dead = true;
        if (snakeYs[0] == numBlocksHigh) dead = true;

        // Eaten itself?
        for (int i = snakeLength - 1; i > 0; i--) {
            if ((i > 4) && (snakeXs[0] == snakeXs[i]) && (snakeYs[0] == snakeYs[i])) {
                dead = true;
            }
        }

        return false;
    }

    public void update() {
        // Did the head of the snake eat Bob?
        if (snakeXs[0] == bobX && snakeYs[0] == bobY) {
            eatBob();
        }

        moveSnake();

        if (detectDeath()) {
            //start again
            soundPool.play(snake_crash, 1, 1, 0, 0, 1);

            newGame();
        }
    }

    public void draw() {
        // Get a lock on the canvas
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();

            // Fill the screen with Game Code School blue
            canvas.drawColor(Color.argb(255, 26, 128, 182));

            // Set the color of the paint to draw the snake white
            paint.setColor(Color.argb(255, 255, 255, 255));

            // Scale the HUD text
            paint.setTextSize(90);
            canvas.drawText("Score:" + score, 10, 70, paint);

            // Draw the snake one block at a time
            for (int i = 0; i < snakeLength; i++) {
                canvas.drawRect(snakeXs[i] * blockSize,
                        (snakeYs[i] * blockSize),
                        (snakeXs[i] * blockSize) + blockSize,
                        (snakeYs[i] * blockSize) + blockSize,
                        paint);
            }

            // Set the color of the paint to draw Bob red
            paint.setColor(Color.argb(255, 255, 0, 0));

            // Draw Bob
            canvas.drawRect(bobX * blockSize,
                    (bobY * blockSize),
                    (bobX * blockSize) + blockSize,
                    (bobY * blockSize) + blockSize,
                    paint);
            paint.setColor(Color.rgb(237, 194, 194));
            canvas.drawRect(dbobX * blockSize,
                    (dbobY * blockSize),
                    (dbobX * blockSize) + blockSize,
                    (dbobY * blockSize) + blockSize,
                    dPaint);
            // Unlock the canvas and reveal the graphics for this frame
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public boolean updateRequired() {

        // Are we due to update the frame
        if(nextFrameTime <= System.currentTimeMillis()){
            // Tenth of a second has passed

            // Setup when the next update will be triggered
            nextFrameTime =System.currentTimeMillis() + MILLIS_PER_SECOND / FPS;

            // Return true so that the update and draw
            // functions are executed
            return true;
        }

        return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent pSceneTouchEvent){
        switch(pSceneTouchEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = pSceneTouchEvent.getX();
                y1 = pSceneTouchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = pSceneTouchEvent.getX();
                y2 = pSceneTouchEvent.getY();
                float deltaX = x2 - x1;
                float deltaY = y2 - y1;
                if (deltaX > MIN_DISTANCE)
                {
                    //swipeLeftToRight();
                    //Log.i("Right"," RIght");
                    if((isUp || isDown) && (!isRight && !isLefft))
                        heading = Heading.RIGHT;

                }
                else if( Math.abs(deltaX) > MIN_DISTANCE)
                {
                    //swipeRightToLeft();
                    //Log.i("Right"," Left");
                    if((isUp || isDown) && (!isRight && !isLefft))
                        heading = Heading.LEFT;
                }
                else if(deltaY > MIN_DISTANCE){
                    //swipeTopToBottom();
                    //Log.i("Right"," Bottom");
                    if((!isUp && !isDown) && (isRight || isLefft))
                        heading = Heading.DOWN;
                }
                else if( Math.abs(deltaY) > MIN_DISTANCE){
                    //swipeBottopmToTop();
                    //Log.i("Right"," TOp");
                    if((!isUp && !isDown) && (isRight || isLefft))
                        heading = Heading.UP;
                }

                break;
        }
        return true;
    }

    /*@Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        this.mDetector.onTouchEvent(motionEvent);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(motionEvent);
        *//*Log.d("touch", "onTouchEvent: "+motionEvent.getAction());*//*

        *//*switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                if (motionEvent.getX() >= screenX / 2) {
                    switch(heading){
                        case UP:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.UP;
                            break;
                    }
                } else {
                    switch(heading){
                        case UP:
                            heading = Heading.LEFT;
                            break;
                        case LEFT:
                            heading = Heading.DOWN;
                            break;
                        case DOWN:
                            heading = Heading.RIGHT;
                            break;
                        case RIGHT:
                            heading = Heading.UP;
                            break;
                    }
                }
        }
        return true;*//*
    }*/
}
