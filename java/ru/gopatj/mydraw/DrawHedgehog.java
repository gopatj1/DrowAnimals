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
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DrawHedgehog extends View {
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
    private boolean correctActionDown = false; // check is drawing start at hedgehog
    private boolean correctDrawingArea = false; // check is drawing at hedgehog + needles area
    private double startDrawingTime; // start drawing time

    /** Need to track this so the dirty region can accommodate the stroke. */
    private static float halfStrokeWidth;

    /** Optimizes painting by invalidating the smallest possible area */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF(); // rectangle area of repainting

    /** Animation objects */
    public static boolean animationEnabled;
    Bitmap hedgehog; // contain picture
    Matrix matrix; // for rotation and move in canvas
    float degrees; // degreesOfSheepHead of rotation
    int hedgehogX, hedgehogY;
    int xSpeed, ySpeed;
    String hedgehogDirection; // left or right
    boolean fromLeftSide;/////////////
    boolean onGround;
    int n, m;

    public DrawHedgehog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.hedgehog_background);

        strokeWidth = screenWidth / 50;
        halfStrokeWidth = strokeWidth / 2;

        // brush attributes
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);

        // Find our tree resource and change dimension
        matrix = new Matrix();
        hedgehog = BitmapFactory.decodeResource(getResources(), R.drawable.hedgehog);
        hedgehog = Bitmap.createScaledBitmap(hedgehog, screenWidth / 5, screenHeight / 4, true);

        // position and animation attributes of tree
        animationEnabled = false; // animation is enable or disable
        //treeX = (int)((displayMetrics.widthPixels * displayMetrics.density) / 8); // y coordinate
        hedgehogX = screenWidth / 8; // y coordinate
        hedgehogY = (int)(screenHeight * 0.55); // y coordinate
        matrix.setTranslate(hedgehogX, hedgehogY); // set position x,y on matrix
        xSpeed = screenWidth / 100;
        ySpeed = 1;
        hedgehogDirection = "right";
        fromLeftSide = false;////////////////
        onGround = true;


        // start firstly finger animation and fix current time
        //HedgehogActivity.startFingerAnimation("firstly");
        startDrawingTime = System.currentTimeMillis();
    }

    /** Undo the last path */
    public void undo() {
        if(!paths.isEmpty())
            paths.remove(paths.size() - 1);
        invalidate(); // repaints the entire view
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDraw(Canvas canvas) {



        // start animation of running tree after time interval or number of needles
        if((System.currentTimeMillis() - startDrawingTime > 20000 && paths.size() >= 3) ||
                paths.size() == 7) {
            animationEnabled = true;
            matrix.setTranslate(hedgehogX += xSpeed, hedgehogY);
            if(hedgehogX > canvas.getWidth())
                paths.clear();
        }
        /*if(animationEnabled) {
            if(!onGround ) {
                matrix.setRotate(degreesOfSheepHead -= 5, drawable.tree.getWidth() / 2, drawable.tree.getHeight() / 2);
                matrix.postTranslate(treeX -= xSpeed, treeY -= ySpeed);
            } else {

                if (treeX >= drawable.tree.getWidth() && onGround && hedgehogDirection.equals("left")) {
                    matrix.setScale(-1, 1); // vertical mirrored bitmap to left
                    matrix.postTranslate(treeX -= xSpeed, treeY);
                    //n++;
                } else {
                    hedgehogDirection = "right";
                    //fromLeftSide = true;
                }

                if ((treeX + drawable.tree.getWidth() <= canvas.getWidth()) && onGround && hedgehogDirection.equals("right")) {
                    matrix.setTranslate(treeX += xSpeed, treeY);
                    //fromLeftSide = false;
                    //n++;
                    //if(m > n / 3)
                        //onGround = false;
                } else {
                    hedgehogDirection = "left";
                    //m++;
                }
            //}
            //System.out.println(fromLeftSide);

            //matrix.setRotate(degreesOfSheepHead -= 5, drawable.tree.getWidth() / 2, drawable.tree.getHeight() / 2);
            //matrix.postTranslate(treeX -= xSpeed, treeY -= ySpeed);
        }*/
        canvas.drawBitmap(hedgehog, matrix, paint); // draw our tree on canvas



        if(!animationEnabled) {
            // Draw each path of array paths with their color and width
            for (Path p : paths) {
                paint.setColor(colorsMap.get(p));
                canvas.drawPath(p, paint);
            }
            paint.setColor(selectedColor); // color of this new path
            canvas.drawPath(path, paint); // draw this new path
        } else {
            for (Path p : paths) {
                //if(hedgehogDirection.equals("right"))
                    p.offset(xSpeed, 0);
                //else if(hedgehogDirection.equals("left"))
                    //p.offset(-xSpeed, 0);

                paint.setColor(colorsMap.get(p));
                canvas.drawPath(p, paint);
            }
            paint.setColor(selectedColor); // color of this new path
            canvas.drawPath(path, paint); // draw this new path
        }


       /* Path path1 = new Path();
        path1.addOval(hedgehogX, hedgehogY, hedgehogX + hedgehog.getWidth(), hedgehogY + hedgehog.getHeight(), null);
        canvas.drawPath(path1, paint);*/

        // redrawing only drawable.tree rectangle area, not all canvas for do not clog the memory
        invalidate(hedgehogX, hedgehogY, hedgehogX + hedgehog.getWidth(), hedgehogY + hedgehog.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        if(animationEnabled) return true; // stop method if tree is animated
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // check is action down in tree area. If incorrect stop method and play finger animation
                ////if(!(eventX >= hedgehogX && eventX <= hedgehogX + hedgehog.getWidth() * 0.75 &&
                ////        eventY >= hedgehogY && eventY <= hedgehogY + hedgehog.getHeight() * 0.85)) {
                if(!(eventX >= hedgehogX && eventX <= hedgehogX + hedgehog.getWidth() * 0.55 &&
                        eventY >= hedgehogY + hedgehog.getHeight() * 0.18 && eventY <= hedgehogY + hedgehog.getHeight() * 0.85) &&
                        !(eventX >= hedgehogX + hedgehog.getWidth() * 0.15 && eventX <= hedgehogX + hedgehog.getWidth() * 0.85 &&
                                eventY >= hedgehogY && eventY <= hedgehogY + hedgehog.getHeight() * 0.15) &&
                        !(eventX >= hedgehogX + hedgehog.getWidth() * 0.55 && eventX <= hedgehogX + hedgehog.getWidth() * 0.68 &&
                                eventY >= hedgehogY + hedgehog.getHeight() * 0.18 && eventY <= hedgehogY + hedgehog.getHeight() * 0.44)) {
                    HedgehogActivity.startFingerAnimation("incorrectActionDown");
                    correctActionDown = false; // ignore action up
                    correctDrawingArea = false; // ignore this drawing path
                    Toast.makeText(getContext(),"Рисование не на ежике", Toast.LENGTH_SHORT).show();
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
                // check is action move in tree area + needles area. If incorrect stop method
                if(!(eventX >= hedgehogX - hedgehog.getWidth() * 0.5 && eventX <= hedgehogX + hedgehog.getWidth() * 1.2 &&
                        eventY >= hedgehogY - hedgehog.getHeight() * 0.5 && eventY <= hedgehogY + hedgehog.getHeight() * 1.2)) {
                    correctDrawingArea = false;
                    //path.reset();
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
                if((!(eventX >= hedgehogX - hedgehog.getWidth() * 0.5 && eventX <= hedgehogX + hedgehog.getWidth() * 1.2 &&
                        eventY >= hedgehogY - hedgehog.getHeight() * 0.5 && eventY <= hedgehogY + hedgehog.getHeight() * 1.2) && correctActionDown) ||
                        (eventX >= hedgehogX - hedgehog.getWidth() * 0.5 && eventX <= hedgehogX + hedgehog.getWidth() * 1.2 &&
                                eventY >= hedgehogY - hedgehog.getHeight() * 0.5 && eventY <= hedgehogY + hedgehog.getHeight() * 1.2 &&
                                correctActionDown) && !correctDrawingArea) {
                    HedgehogActivity.startFingerAnimation("incorrectActionDown");
                    correctDrawingArea = false;

                    // delete all lines of path or draw lines only in correct area
                    //path.reset(); // delete all lines
                    paths.add(path); // add this path to array paths
                    colorsMap.put(path, selectedColor); // store the color of this path
                    path = new Path();

                    Toast.makeText(getContext(),"Иголки слишком длинные", Toast.LENGTH_SHORT).show();
                    return true;
                }
                if(!correctDrawingArea) return true; // stop method if incorrect action down
                // Use +1 if ACTION_DOWN(x;y) the same as ACTION_UP(x;y) for correct work on oldest android versions
                path.lineTo(eventX + 1, eventY + 1);
                paths.add(path); // add this path to array paths
                colorsMap.put(path, selectedColor); // store the color of this path
                path = new Path();
                correctDrawingArea = true;
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