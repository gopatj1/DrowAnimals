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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DrawTree extends View{

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
    private double startDrawingTime; // start drawing time

    /** Need to track this so the dirty region can accommodate the stroke. */
    private static float halfStrokeWidth;

    /** Optimizes painting by invalidating the smallest possible area */
    private float lastTouchX;
    private float lastTouchY;
    private final RectF dirtyRect = new RectF(); // rectangle area of repainting

    /** Animation objects */
    public static boolean animationEnabled;
    Bitmap tree; // contain picture
    Bitmap snow; // contain picture
    ArrayList<Bitmap> snowList = new ArrayList<>(); // using for falling snow
    Map<Bitmap, Integer> snowMapX = new HashMap<Bitmap, Integer>(); // using for falling snow
    Map<Bitmap, Integer> snowMapY = new HashMap<Bitmap, Integer>(); // using for falling snow
    Matrix matrix; // for rotation and move in canvas
    //Matrix matrixSnow; // for rotation and move in canvas
    float degrees; // degreesOfSheepHead of rotation
    int treeX, treeY;
    int snowX, snowY;
    int snowRotate;
    /////////String hedgehogDirection; // left or right
    /////////boolean fromLeftSide;/////////////
    /////////boolean onGround;
    /////////int n, m;

    public DrawTree(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.tree_background);

        strokeWidth = displayMetrics.widthPixels / 40;
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
        tree = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
        snow = BitmapFactory.decodeResource(getResources(), R.drawable.snow);
        tree = Bitmap.createScaledBitmap(tree, screenWidth / 4, screenHeight / 2, true);
        snow = Bitmap.createScaledBitmap(snow, (int)(screenWidth * 0.07), (int)(screenWidth * 0.07), true);

        // position and animation attributes of tree
        animationEnabled = false; // animation is enable or disable
        //treeX = (int)((displayMetrics.widthPixels * displayMetrics.density) / 8); // y coordinate
        treeX = screenWidth / 5; // y coordinate
        treeY = (int)(screenHeight * 0.4); // y coordinate
        matrix.setTranslate(treeX, treeY); // set position x,y on matrix

        startDrawingTime = System.currentTimeMillis();
    }

    /** Undo the last path */
    public void undo() {
        if(!paths.isEmpty())
            paths.remove(paths.size() - 1);
        invalidate(); // repaints the entire view
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // start firstly finger animation and fix current time
        //TreeActivity.startFingerAnimation("firstly");

        // start animation of snow
        //matrixSnow.setRotate(snowRotate += 1, (int)(snow.getWidth() / 1.5), (int)(snow.getHeight() / 1.5));
        //matrixSnow.postTranslate(snowX, snowY += 3);

        // start animation of santa and fireworks after time interval or number of needles
        if((System.currentTimeMillis() - startDrawingTime > 30000 && paths.size() >= 5) ||
                paths.size() == 10) {
            if(!animationEnabled)
                TreeActivity.startSantaAnimation();
            animationEnabled = true;
            //matrix.setTranslate(treeX += xSpeed, treeY);
            //if(treeX > canvas.getWidth())
            //    paths.clear();
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
        canvas.drawBitmap(tree, matrix, paint); // draw our tree on canvas



        //if(!animationEnabled) {
            // Draw each path of array paths with their color and width
            for (Path p : paths) {
                paint.setColor(colorsMap.get(p));
                canvas.drawPath(p, paint);
            }
            paint.setColor(selectedColor); // color of this new path
            canvas.drawPath(path, paint); // draw this new path
        /*} else {
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
        }*/


        // animation of falling snow
        Iterator iter = snowList.iterator(); // use iterator instead foreach for exclude ConcurrentModificationException
        while (iter.hasNext()) {
            Bitmap snowElement = (Bitmap) iter.next();
            //snowMap.get(snowElement).setRotate(50, (int)(snowElement.getWidth() / 1.5), (int)(snowElement.getHeight() / 1.5));
            //snowMap.get(snowElement).postTranslate(snowMapX.get(snowElement), snowMapY.get(snowElement) + 1);
            //canvas.drawBitmap(snowElement, snowMap.get(snowElement), paint);

            snowMapY.put(snowElement, snowMapY.get(snowElement) + 4); // move snow down
            // if snow go out from screen delete it
            if(snowMapY.get(snowElement) > screenHeight) {
                snowMapX.remove(snowElement); // delete x pos from collection
                snowMapY.remove(snowElement); // delete y pos from collection
                //snowList.remove(snowElement);
                iter.remove(); // delete bitmap element from collection
            } else // draw snow
                canvas.drawBitmap(snowElement, snowMapX.get(snowElement), snowMapY.get(snowElement), paint);
        }

        // redrawing only drawable.tree rectangle area, not all canvas for do not clog the memory
        invalidate(treeX, treeY, treeX + tree.getWidth(), treeY + tree.getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        if(animationEnabled) return true; // stop method if tree is animated
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // check is action down in tree area. If incorrect stop method and play finger animation
                if(!(eventX >= treeX && eventX <= treeX + tree.getWidth() &&
                        eventY >= treeY && eventY <= treeY + tree.getHeight())) {
                    //TreeActivity.startFingerAnimation("incorrectActionDown");
                    //Toast.makeText(getContext(),"Рисование не на ёлке", Toast.LENGTH_SHORT).show();

                    // spawn new snow in event position
                    snowX = (int) eventX - snow.getWidth() / 2;
                    snowY = (int) eventY - snow.getHeight() / 2;
                    snow = BitmapFactory.decodeResource(getResources(), R.drawable.snow); // new snow
                    snow = Bitmap.createScaledBitmap(snow, (int)(screenWidth * 0.07), (int)(screenWidth * 0.07), true); // the same size
                    snowList.add(snow); // add to bitmap list
                    snowMapX.put(snow, snowX); // add to map with coordinates
                    snowMapY.put(snow, snowY); // add to map with coordinates

                    return true;
                }
                path.moveTo(eventX, eventY);
                lastTouchX = eventX;
                lastTouchY = eventY;

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
                // Use +1 if ACTION_DOWN(x;y) the same as ACTION_UP(x;y) for correct work on oldest android versions
                path.lineTo(eventX + 1, eventY + 1);

                paths.add(path); // add this path to array paths
                colorsMap.put(path,selectedColor); // store the color of this path
                path = new Path();
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
