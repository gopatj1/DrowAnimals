package ru.gopatj.mydraw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawSheep extends View {

    /** Screen dimension */
    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    int screenWidth = displayMetrics.widthPixels;
    int screenHeight = displayMetrics.heightPixels;

    /** Objects for drawing */
    private ArrayList<Path> paths = new ArrayList<>(); // using for undo/redo and different color drawing
    private Map<Path, Integer> colorsMap = new HashMap<Path, Integer>(); // color of each path
    private Paint paint; // like a brush(color, width and ect.)
    private Path path; // create difficult shape of our touch and move on screen events
    public static float strokeWidth; // width of brush
    public static int selectedColor = Color.RED; // paint color
    private boolean correctActionDown = false; // check is drawing start at sheep
    private boolean correctDrawingArea = false; // check is drawing at shep + wool area
    private double startDrawingTime; // start drawing time

    /** Need to track this so the dirty region can accommodate the stroke. */
    private static float halfStrokeWidth;

    /** Optimizes painting by invalidating the smallest possible area */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF(); // rectangle area of repainting

    /** Animation objects */
    public static boolean animationEnabled;
    Bitmap sheepHead; // contain picture
    Bitmap windmill; // contain picture
    Bitmap clouds; // contain picture
    Bitmap sheepBackground; // contain picture
    Matrix matrixSheepHead; // for rotation and move in canvas
    Matrix matrixWindmill; // for rotation and move in canvas
    Matrix matrixClouds; // for rotation and move in canvas
    int sheepHeadX, sheepHeadY;
    int windmillX, windmillY;
    int cloudsX, cloudsY;
    float degreesOfSheepHead; // for rotate sheep head
    String rotateDirection = "left"; //left or right rotate
    float degreesOfWindmill; // for rotate windmill
    int cloudsSpeedX;

    public DrawSheep(Context context, AttributeSet attrs) {
        super(context, attrs);
        //setBackgroundResource(R.drawable.sheep_background);

        strokeWidth = screenWidth / 200;
        halfStrokeWidth = strokeWidth / 2;

        // brush attributes
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);

        // Find our sheepHead, windmill and clouds resource and change dimension
        matrixSheepHead = new Matrix();
        matrixWindmill = new Matrix();
        matrixClouds = new Matrix();
        sheepHead = BitmapFactory.decodeResource(getResources(), R.drawable.sheep_head);
        windmill = BitmapFactory.decodeResource(getResources(), R.drawable.windmill);
        clouds = BitmapFactory.decodeResource(getResources(), R.drawable.clouds);
        sheepBackground = BitmapFactory.decodeResource(getResources(), R.drawable.sheep_background);
        sheepHead = Bitmap.createScaledBitmap(sheepHead, screenWidth / 6, screenHeight / 4, true);
        windmill = Bitmap.createScaledBitmap(windmill, screenWidth / 8, screenHeight / 5, true);
        clouds = Bitmap.createScaledBitmap(clouds, screenWidth / 8, screenHeight / 6, true);

        //int s = screenHeight/(screenHeight / sheepBackground.getHeight());
        //System.out.print(s);
        sheepBackground = Bitmap.createScaledBitmap(sheepBackground, screenHeight, screenHeight, true);

        // position and animation attributes
        animationEnabled = false; // animation is enable or disable
        sheepHeadX = displayMetrics.widthPixels / 6; // y coordinate
        sheepHeadY = displayMetrics.heightPixels / 2; // y coordinate
        windmillX = (int) (displayMetrics.widthPixels / 5.5); // y coordinate
        windmillY = displayMetrics.heightPixels / 8; // y coordinate
        cloudsX = screenWidth / 6; // y coordinate
        cloudsY = screenHeight / 8; // y coordinate
        matrixSheepHead.setTranslate(sheepHeadX, sheepHeadY); // set position x,y on matrix
        matrixWindmill.setTranslate(windmillX, windmillY); // set position x,y on matrix
        matrixClouds.setTranslate(cloudsX, cloudsY); // set position x,y on matrix
        cloudsSpeedX = 1;

        startDrawingTime = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // start firstly finger animation and fix current time
        //SheepActivity.startFingerAnimation("firstly");

        // start animation of sheep head after time interval or number of paths
        if(paths.size() > 0) {
            if (rotateDirection.equals("left"))
                if (degreesOfSheepHead > -45) {
                    matrixSheepHead.setRotate(degreesOfSheepHead -= 1.5, sheepHead.getWidth() / 2, sheepHead.getHeight() / 2);
                    matrixSheepHead.postTranslate(sheepHeadX, sheepHeadY);
                }
                else rotateDirection = "right";
            else if (rotateDirection.equals("right"))
                if (degreesOfSheepHead < 45) {
                    matrixSheepHead.setRotate(degreesOfSheepHead += 1.5, sheepHead.getWidth() / 2, sheepHead.getHeight() / 2);
                    matrixSheepHead.postTranslate(sheepHeadX, sheepHeadY);
                }
                else rotateDirection = "left";
        }

        // start animation of windmill after time interval or number of paths
        if(System.currentTimeMillis() - startDrawingTime > 15000 || paths.size() >= 5) {
            matrixWindmill.setRotate(degreesOfWindmill += 2, windmill.getWidth() / 2, windmill.getHeight() / 2);
            matrixWindmill.postTranslate(windmillX, windmillY);
        }

        // start animation of clouds after time interval or number of paths
        if(System.currentTimeMillis() - startDrawingTime > 25000 || paths.size() >= 10) {
            matrixClouds.setTranslate(cloudsX += cloudsSpeedX, cloudsY);
            if(cloudsX > screenWidth - screenWidth * 0.34 - clouds.getWidth()) {
                cloudsX = screenWidth / 6;
                matrixClouds.setTranslate(cloudsX, cloudsY);
            }
        }

        // start animation of magic after number of paths
        if(paths.size() >= 15 && !animationEnabled) {
            SheepActivity.startMagicAnimation();

            animationEnabled = true; // now cant drawing
        }

        canvas.drawBitmap(sheepBackground, canvas.getWidth() / 2 - sheepBackground.getWidth() / 2, 0, paint); // draw picture on canvas
        canvas.drawBitmap(sheepHead, matrixSheepHead, paint); // draw picture on canvas
        canvas.drawBitmap(clouds, matrixClouds, paint); // draw picture on canvas
        canvas.drawBitmap(windmill, matrixWindmill, paint); // draw picture on canvas

        // draw wool of sheep
        for (Path p : paths) {
            paint.setColor(colorsMap.get(p));
            canvas.drawPath(p, paint);
        }
        paint.setColor(selectedColor); // color of this new path
        canvas.drawPath(path, paint); // draw this new path

        // redrawing only sheep rectangle area, not all canvas for do not clog the memory
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        if(animationEnabled) return true; // stop method if tree is animated
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // check is action down in sheep area. If incorrect stop method and play finger animation
                if(!(eventX >= screenWidth * 0.3 && eventX <= screenWidth * 0.6 &&
                        eventY >= screenHeight * 0.4 && eventY <= screenHeight * 0.75)) {
                    SheepActivity.startFingerAnimation("incorrectActionDown");
                    correctActionDown = false; // ignore action up
                    correctDrawingArea = false; // ignore this drawing path
                    Toast.makeText(getContext(),"Рисование не на овце", Toast.LENGTH_SHORT).show();
                    return true;
                }
                correctActionDown = true;
                correctDrawingArea = true;
                path.reset();
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;
                return true;
            case MotionEvent.ACTION_MOVE:
                // check is action move in sheep area area. If incorrect stop method
                if(!(eventX >= screenWidth * 0.3 && eventX <= screenWidth * 0.6 &&
                        eventY >= screenHeight * 0.4 && eventY <= screenHeight * 0.75)) {
                    correctDrawingArea = false;
                    path.reset();
                    return true;
                }
                if(!correctDrawingArea) return true; // stop method if incorrect action down

                // Start tracking the dirty region
                resetDirtyRect(eventX, eventY);
                // When the hardware tracks events faster than they are delivered, the
                // event will contain a history of those skipped points
                int historySize = event.getHistorySize();
                for (int i = 0; i < historySize; i++) {
                    float historicalX = event.getHistoricalX(i);
                    float historicalY = event.getHistoricalY(i);
                    expandDirtyRect(historicalX, historicalY);
                    path.lineTo(historicalX, historicalY);
                }
                // After replaying history, connect the line to the touch point.
                path.lineTo(eventX, eventY);
                correctDrawingArea = true;
                break;
            case MotionEvent.ACTION_UP:
                // check is action up in tree area + needles area and is action down in tree area. If incorrect stop method and play finger animation
                if(!correctDrawingArea && correctActionDown) {
                    SheepActivity.startFingerAnimation("incorrectActionDown");
                    path.reset();
                    Toast.makeText(getContext(),"Шерсть должна быть на овце", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(correctActionDown) {
                    // Use +1 if ACTION_DOWN(x;y) the same as ACTION_UP(x;y) for correct work on oldest android versions
                    path.lineTo(eventX + 1, eventY + 1);
                    paths.add(path); // add this path to array paths
                    colorsMap.put(path, selectedColor); // store the color of this path
                    path = new Path();
                    correctDrawingArea = true;
                }
                break;

            default:
                Log.d("DrawLog", "Ignored touch event: " + event.toString());
                return false;
        }

        // Include half the stroke width to avoid clipping and redrawing
        invalidate(
                (int) (dirtyRect.left - halfStrokeWidth),
                (int) (dirtyRect.top - halfStrokeWidth),
                (int) (dirtyRect.right + halfStrokeWidth),
                (int) (dirtyRect.bottom + halfStrokeWidth));
        lastTouchX = eventX;
        lastTouchY = eventY;
        return true;
    }

    /** Called when replaying history to ensure the dirty region includes all points */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    /** Resets the dirty region when the motion event occurs */
    private void resetDirtyRect(float eventX, float eventY) {
        // The lastTouchX and lastTouchY were set when the ACTION_DOWN
        // motion event occurred
        dirtyRect.left = Math.min(lastTouchX, eventX);
        dirtyRect.right = Math.max(lastTouchX, eventX);
        dirtyRect.top = Math.min(lastTouchY, eventY);
        dirtyRect.bottom = Math.max(lastTouchY, eventY);
    }

    public static void setPaintColor (int color) {
        selectedColor = color;
    }

    public static void setStrokeWidth (float strokeWidth) {
        //paint.setStrokeWidth(strokeWidth);
    }

    public static void setAnimationEnableStatus (boolean status) {
        animationEnabled = status;
    }

    public static boolean getAnimationEnableStatus () {
        return animationEnabled;
    }
}
