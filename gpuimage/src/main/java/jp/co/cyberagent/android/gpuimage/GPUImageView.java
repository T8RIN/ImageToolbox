/*
 * Copyright (C) 2018 CyberAgent, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jp.co.cyberagent.android.gpuimage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.Semaphore;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.util.Rotation;

import static jp.co.cyberagent.android.gpuimage.GPUImage.SURFACE_TYPE_SURFACE_VIEW;
import static jp.co.cyberagent.android.gpuimage.GPUImage.SURFACE_TYPE_TEXTURE_VIEW;

public class GPUImageView extends FrameLayout {

    private int surfaceType = SURFACE_TYPE_SURFACE_VIEW;
    private View surfaceView;
    private GPUImage gpuImage;
    private boolean isShowLoading = true;
    private GPUImageFilter filter;
    public Size forceSize = null;
    private float ratio = 0.0f;

    public final static int RENDERMODE_WHEN_DIRTY = 0;
    public final static int RENDERMODE_CONTINUOUSLY = 1;

    public GPUImageView(Context context) {
        super(context);
        init(context, null);
    }

    public GPUImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GPUImageView, 0, 0);
            try {
                surfaceType = a.getInt(R.styleable.GPUImageView_gpuimage_surface_type, surfaceType);
                isShowLoading = a.getBoolean(R.styleable.GPUImageView_gpuimage_show_loading, isShowLoading);
            } finally {
                a.recycle();
            }
        }
        gpuImage = new GPUImage(context);
        if (surfaceType == SURFACE_TYPE_TEXTURE_VIEW) {
            surfaceView = new GPUImageGLTextureView(context, attrs);
            gpuImage.setGLTextureView((GLTextureView) surfaceView);
        } else {
            surfaceView = new GPUImageGLSurfaceView(context, attrs);
            gpuImage.setGLSurfaceView((GLSurfaceView) surfaceView);
        }
        addView(surfaceView);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio != 0.0f) {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);

            int newHeight;
            int newWidth;
            if (width / ratio < height) {
                newWidth = width;
                newHeight = Math.round(width / ratio);
            } else {
                newHeight = height;
                newWidth = Math.round(height * ratio);
            }

            int newWidthSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.EXACTLY);
            int newHeightSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.EXACTLY);
            super.onMeasure(newWidthSpec, newHeightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * Retrieve the GPUImage instance used by this view.
     *
     * @return used GPUImage instance
     */
    public GPUImage getGPUImage() {
        return gpuImage;
    }

    /**
     * Deprecated: Please call
     * {@link GPUImageView#updatePreviewFrame(byte[], int, int)} frame by frame
     * <p>
     * Sets the up camera to be connected to GPUImage to get a filtered preview.
     *
     * @param camera the camera
     */
    @Deprecated
    public void setUpCamera(final Camera camera) {
        gpuImage.setUpCamera(camera);
    }

    /**
     * Deprecated: Please call
     * {@link GPUImageView#updatePreviewFrame(byte[], int, int)} frame by frame
     * <p>
     * Sets the up camera to be connected to GPUImage to get a filtered preview.
     *
     * @param camera         the camera
     * @param degrees        by how many degrees the image should be rotated
     * @param flipHorizontal if the image should be flipped horizontally
     * @param flipVertical   if the image should be flipped vertically
     */
    @Deprecated
    public void setUpCamera(final Camera camera, final int degrees, final boolean flipHorizontal,
                            final boolean flipVertical) {
        gpuImage.setUpCamera(camera, degrees, flipHorizontal, flipVertical);
    }

    /**
     * Update camera preview frame with YUV format data.
     *
     * @param data   Camera preview YUV data for frame.
     * @param width  width of camera preview
     * @param height height of camera preview
     */
    public void updatePreviewFrame(byte[] data, int width, int height) {
        gpuImage.updatePreviewFrame(data, width, height);
    }

    /**
     * Sets the background color
     *
     * @param red   red color value
     * @param green green color value
     * @param blue  red color value
     */
    public void setBackgroundColor(float red, float green, float blue) {
        gpuImage.setBackgroundColor(red, green, blue);
    }

    /**
     * Set the rendering mode. When renderMode is
     * RENDERMODE_CONTINUOUSLY, the renderer is called
     * repeatedly to re-render the scene. When renderMode
     * is RENDERMODE_WHEN_DIRTY, the renderer only rendered when the surface
     * is created, or when {@link #requestRender} is called. Defaults to RENDERMODE_CONTINUOUSLY.
     *
     * @param renderMode one of the RENDERMODE_X constants
     * @see #RENDERMODE_CONTINUOUSLY
     * @see #RENDERMODE_WHEN_DIRTY
     * @see GLSurfaceView#setRenderMode(int)
     * @see GLTextureView#setRenderMode(int)
     */
    public void setRenderMode(int renderMode) {
        if (surfaceView instanceof GLSurfaceView) {
            ((GLSurfaceView) surfaceView).setRenderMode(renderMode);
        } else if (surfaceView instanceof GLTextureView) {
            ((GLTextureView) surfaceView).setRenderMode(renderMode);
        }
    }

    // TODO Should be an xml attribute. But then GPUImage can not be distributed as .jar anymore.
    public void setRatio(float ratio) {
        this.ratio = ratio;
        surfaceView.requestLayout();
        gpuImage.deleteImage();
    }

    /**
     * Set the scale type of GPUImage.
     *
     * @param scaleType the new ScaleType
     */
    public void setScaleType(GPUImage.ScaleType scaleType) {
        gpuImage.setScaleType(scaleType);
    }

    /**
     * Sets the rotation of the displayed image.
     *
     * @param rotation new rotation
     */
    public void setRotation(Rotation rotation) {
        gpuImage.setRotation(rotation);
        requestRender();
    }

    /**
     * Set the filter to be applied on the image.
     *
     * @param filter Filter that should be applied on the image.
     */
    public void setFilter(GPUImageFilter filter) {
        this.filter = filter;
        gpuImage.setFilter(filter);
        requestRender();
    }

    /**
     * Get the current applied filter.
     *
     * @return the current filter
     */
    public GPUImageFilter getFilter() {
        return filter;
    }

    /**
     * Sets the image on which the filter should be applied.
     *
     * @param bitmap the new image
     */
    public void setImage(final Bitmap bitmap) {
        gpuImage.setImage(bitmap);
    }

    /**
     * Sets the image on which the filter should be applied from a Uri.
     *
     * @param uri the uri of the new image
     */
    public void setImage(final Uri uri) {
        gpuImage.setImage(uri);
    }

    /**
     * Sets the image on which the filter should be applied from a File.
     *
     * @param file the file of the new image
     */
    public void setImage(final File file) {
        gpuImage.setImage(file);
    }

    public void requestRender() {
        if (surfaceView instanceof GLSurfaceView) {
            ((GLSurfaceView) surfaceView).requestRender();
        } else if (surfaceView instanceof GLTextureView) {
            ((GLTextureView) surfaceView).requestRender();
        }
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName   the file name
     * @param listener   the listener
     */
    public void saveToPictures(final String folderName, final String fileName,
                               final OnPictureSavedListener listener) {
        new SaveTask(folderName, fileName, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Save current image with applied filter to Pictures. It will be stored on
     * the default Picture folder on the phone below the given folderName and
     * fileName. <br>
     * This method is async and will notify when the image was saved through the
     * listener.
     *
     * @param folderName the folder name
     * @param fileName   the file name
     * @param width      requested output width
     * @param height     requested output height
     * @param listener   the listener
     */
    public void saveToPictures(final String folderName, final String fileName,
                               int width, int height,
                               final OnPictureSavedListener listener) {
        new SaveTask(folderName, fileName, width, height, listener).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * Retrieve current image with filter applied and given size as Bitmap.
     *
     * @param width  requested Bitmap width
     * @param height requested Bitmap height
     * @return Bitmap of picture with given size
     * @throws InterruptedException
     */
    public Bitmap capture(final int width, final int height) throws InterruptedException {
        // This method needs to run on a background thread because it will take a longer time
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("Do not call this method from the UI thread!");
        }

        forceSize = new Size(width, height);

        final Semaphore waiter = new Semaphore(0);

        // Layout with new size
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                waiter.release();
            }
        });

        post(new Runnable() {
            @Override
            public void run() {
                // Optionally, show loading view:
                if (isShowLoading) {
                    addView(new LoadingView(getContext()));
                }
                // Request layout to release waiter:
                surfaceView.requestLayout();
            }
        });

        waiter.acquire();

        // Run one render pass
        gpuImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                waiter.release();
            }
        });
        requestRender();
        waiter.acquire();
        Bitmap bitmap = capture();


        forceSize = null;
        post(new Runnable() {
            @Override
            public void run() {
                surfaceView.requestLayout();
            }
        });
        requestRender();

        if (isShowLoading) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Remove loading view
                    removeViewAt(1);
                }
            }, 300);
        }

        return bitmap;
    }

    /**
     * Capture the current image with the size as it is displayed and retrieve it as Bitmap.
     *
     * @return current output as Bitmap
     * @throws InterruptedException
     */
    public Bitmap capture() throws InterruptedException {
        final Semaphore waiter = new Semaphore(0);

        final int width = surfaceView.getMeasuredWidth();
        final int height = surfaceView.getMeasuredHeight();

        // Take picture on OpenGL thread
        final Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        gpuImage.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                GPUImageNativeLibrary.adjustBitmap(resultBitmap);
                waiter.release();
            }
        });
        requestRender();
        waiter.acquire();

        return resultBitmap;
    }

    /**
     * Pauses the Surface.
     */
    public void onPause() {
        if (surfaceView instanceof GLSurfaceView) {
            ((GLSurfaceView) surfaceView).onPause();
        } else if (surfaceView instanceof GLTextureView) {
            ((GLTextureView) surfaceView).onPause();
        }
    }

    /**
     * Resumes the Surface.
     */
    public void onResume() {
        if (surfaceView instanceof GLSurfaceView) {
            ((GLSurfaceView) surfaceView).onResume();
        } else if (surfaceView instanceof GLTextureView) {
            ((GLTextureView) surfaceView).onResume();
        }
    }

    public static class Size {
        int width;
        int height;

        public Size(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    private class GPUImageGLSurfaceView extends GLSurfaceView {
        public GPUImageGLSurfaceView(Context context) {
            super(context);
        }

        public GPUImageGLSurfaceView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (forceSize != null) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(forceSize.width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(forceSize.height, MeasureSpec.EXACTLY));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private class GPUImageGLTextureView extends GLTextureView {
        public GPUImageGLTextureView(Context context) {
            super(context);
        }

        public GPUImageGLTextureView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (forceSize != null) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(forceSize.width, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(forceSize.height, MeasureSpec.EXACTLY));
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    private class LoadingView extends FrameLayout {
        public LoadingView(Context context) {
            super(context);
            init();
        }

        public LoadingView(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public LoadingView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            init();
        }

        private void init() {
            ProgressBar view = new ProgressBar(getContext());
            view.setLayoutParams(
                    new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            addView(view);
            setBackgroundColor(Color.BLACK);
        }
    }

    private class SaveTask extends AsyncTask<Void, Void, Void> {
        private final String folderName;
        private final String fileName;
        private final int width;
        private final int height;
        private final OnPictureSavedListener listener;
        private final Handler handler;

        public SaveTask(final String folderName, final String fileName,
                        final OnPictureSavedListener listener) {
            this(folderName, fileName, 0, 0, listener);
        }

        public SaveTask(final String folderName, final String fileName, int width, int height,
                        final OnPictureSavedListener listener) {
            this.folderName = folderName;
            this.fileName = fileName;
            this.width = width;
            this.height = height;
            this.listener = listener;
            handler = new Handler();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            try {
                Bitmap result = width != 0 ? capture(width, height) : capture();
                saveImage(folderName, fileName, result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        private void saveImage(final String folderName, final String fileName, final Bitmap image) {
            File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File file = new File(path, folderName + "/" + fileName);
            try {
                file.getParentFile().mkdirs();
                image.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
                MediaScannerConnection.scanFile(getContext(),
                        new String[]{
                                file.toString()
                        }, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(final String path, final Uri uri) {
                                if (listener != null) {
                                    handler.post(new Runnable() {

                                        @Override
                                        public void run() {
                                            listener.onPictureSaved(uri);
                                        }
                                    });
                                }
                            }
                        });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public interface OnPictureSavedListener {
        void onPictureSaved(Uri uri);
    }
}
