# MyCamera
> 本文是基于camera的自定义相机。相信有开发相机经验的同学都知道，在代码里camera相关类和方法都是划横线的。因为在5.0后google舍弃了camera,有了camera2。但是对于定制化不深的，或者功能不复杂的亦或者是应用还可能覆盖在4.0以下的手机的还是可以使用camera的。

在开始本文之前，肯定是要加权限的。之后不再赘述，可点击链接下载了解
<br>

## 一、Android调用系统相机（适配7.0以上）
### 1.1、简单调用系统相机，并获取缩略图
效果图如下：

<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e78702bf1530b?w=480&h=849&f=gif&s=4919341"  height = "500" />

代码很简单：

```java
//就2句代码
Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
startActivityForResult(intent, CAMERA_RESULT);


//在onActivityResult直接可以拿到缩略图
@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_RESULT) {
                Bundle bundle = data.getExtras();
                //这里就是得到的缩略图。因为是intent传递的，放置oom，android已经处理成了缩略图，你会发现缩略图确实是很模糊的
                Bitmap bitmap = (Bitmap) bundle.get("data");
                binding.image.setImageBitmap(bitmap);
            }
        }
    }
```
<br>

### 1.2、调用系统相机，并获取原图
效果图如下：

<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e793ba5ec0f71?w=482&h=850&f=gif&s=4946182"  height = "500" />

因为这里有用到Uri去调用，如果不做处理，在7.0以上的手机是会直接报错的。首先，我们新建xml命名的文件夹，然后新建一个xml,我这里命名为：provider_paths。
```xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <files-path name="my_images" path="." />
    <external-path name="camera_photos" path="."  />
</paths>
```

<br>
在清单文件里配置下，

```java
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.lihang.mycamera.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/provider_paths" />
</provider>
```
<br>

代码里调用如下：
```java
    Intent intent_2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    Uri photoUri = null;
    //如果是大于7.0以上的手机用provider方法，否则正常使用。
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        photoUri = FileProvider.getUriForFile(this, "com.lihang.mycamera.fileprovider", newFile);
        // 给目标应用一个临时授权
        intent_2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            photoUri = Uri.fromFile(newFile);
        }
    intent_2.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
    startActivityForResult(intent_2, CAMERA_PATH);
```

上面代码里的newFile是我生成的：newFile = new File(getExternalCacheDir(), "output_image.jpg");所以在我们新建的那个xml里用到了   external-path name="camera_photos" path="."  。具体介绍可以查看此链接[Android 7.0适配-应用之间共享文件(FileProvider)](https://www.jianshu.com/p/55eae30d133c)

<br>
<br>

## 二、自定义Camera相机
进入我们的主题，说道自定义相机，简单的来说可以说是2个类：
* android.hardware.Camera  ------>  用我的话的理解，就像小时候播放电影的 播放器
* SurfaceView（surfaceHolder）  ------>  用我的话理解，电源的画面，和内容都在存在这个sufaceView里，类似电影带，胶卷。surfaceHolder = surfaceView.getHolder()

看到这里根据我的思路，咱们一起来自定义这个相机。

<br>

### 2.1、新建相机管理类CameraInterface
为什么不用manager,因为camera2 api中确实有这个类。避免重复。这个类是管理camera的。首先他是个单例：
```java
public class CameraInterface {
    private static CameraInterface mCameraInterface;
    private Context context;
    
    private CameraInterface(Context context) {
        this.context = context;
    }
    
    public static synchronized CameraInterface getInstance(Context context) {
        if (mCameraInterface == null) {
            mCameraInterface = new CameraInterface(context);
        }
        return mCameraInterface;
    }
}
```
<br>

### 2.2、doOpenCamera()里，new我们的Camera
在CameraInterface类里，首先我们要有个camera。现实里要有个播放器要花钱买，在java里直接new一个就可以了
```java
    private Camera mCamera;
    //mCamera各项属性参数
    private Camera.Parameters parameters;
    public void doOpenCamera() {
        try {
            //open里还可以带参数，0，代表后置摄像头。1，代表前置摄像头
            mCamera = Camera.open();
            //获得相机参数
            parameters = mCamera.getParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```
<br>

### 2.3、doStartPreview()里，开始预览。当然在这之前有一步设置相机参数setParameters()，放后面讲
```java
    //预览当然要胶卷了，我们直接先传个参进来。我们先把这边管理类讲完
    public void doStartPreview(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            try {
                //设置相机参数，后面讲
                setParameters();
                //这里要注意系统默认的横屏的。我们要将其转换成竖屏，旋转90°
                mCamera.setDisplayOrientation(90);
                //把胶卷放进播放器
                mCamera.setPreviewDisplay(surfaceHolder);
                //开启预览
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
```
<br>

### 2.4、doStopCamera(),离开界面的时候，释放camera
自此大致流程走完了，这样看是不是很简单，接下来看我们的重点设置相机参数
```java
    public void doStopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
```
<br>

### 2.5、setParameters()设置相机参数

```java

public void setParameters() {
    //设置jpg格式
    parameters.setPictureFormat(ImageFormat.JPEG);    
    //设置自动聚焦
    //parameters.setFocusMode(Camera.Parameters.Camera.Parameters.FOCUS_MODE_AUTO);//这行代码居然不起作用
    List<String> focusModes = parameters.getSupportedFocusModes();
    //判断当前设备支持这个聚焦模式
    if (focusModes.contains("continuous-video")) {
        //这个聚焦，才会自动聚焦。做的再好一点是利用传感器，当手指停止手机不动的时候。主动调用一次聚焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
    }
    
    //获得相机和图片尺寸的最佳尺寸
    parameters.setPreviewSize(rightWith, rightHeight);
    parameters.setPictureSize(rightWith, rightHeight);
    mCamera.setParameters(parameters);
}
```
<br>

怎么获取这个最佳尺寸rightWith, rightHeight ?获取我们设备支持的所有相机预览尺寸和图片尺寸，每个设备可能都不一样的。
```java
        List<Camera.Size> listPreview = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < listPreview.size(); i++) {
            Size size = listPreview.get(i);
            Log.i("相机支持预览尺寸", "previewSizes:  " + size.width + " x " + size.height);
        }
        
        List<Camera.Size> listPicture = parameters.getSupportedPreviewSizes();
        for (int i = 0; i < listPicture.size(); i++) {
            Size size = listPicture.get(i);
            Log.i("图片支持预览尺寸", "previewSizes:  " + size.width + " x " + size.height);
        }
```
我的测试机的相机尺寸支持如下：

<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e7d304d113a8b?w=431&h=419&f=png&s=66055"  height = "" />  

我的测试机图片尺寸支持如下：

<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e7d6d7f4070d3?w=389&h=225&f=png&s=14462"  height = "" />  


> 查询了很多资料。大部分都是说用屏幕宽高比例 在这些尺寸中找到一个比差在 小于0.3f的即可。经过我自己的摸索，我觉得应该找一个高分辨的率的最佳，这样也能保质保量。所以我把这些都降序排列后，在预览尺寸和图片尺寸中找到第一个相同的尺寸。即满足要求，也是当前最清晰的尺寸。在我手机里即是1920 x 1080


<br>

### 2.6 拍照
```java
        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                boolean success = false;
                if (data != null && data.length > 0) {
                    success = true;
                }
                //点击拍照，停止预览
                mCamera.stopPreview();
                //这个是图片截图用的。
                String path = savePicture(data);
                //这个callback是我自定义的一个参数，把结果带出去
                callback.onCapture(success, path);
            }
        });
```
<br>

这里唯一需要注意的是，拍照后生成的图片，也就是在savePicture()方法里。要根据前后置摄像头去旋转生成的图篇
```java
            if (cameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                rotatedBitmap = ImageUtil.getRotateBitmap(b, 90.0f);
            } else if (cameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                rotatedBitmap = ImageUtil.getRotateBitmap_front(b, -90.0f);
            }
```
<br>

这里还一个重点是，当处于前置的时候，生成的图片，总是左右相反。看看我们的getRotateBitmap_front方法
```java
    public static Bitmap getRotateBitmap_front(Bitmap b, float rotateDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateDegree);
        //postScale 解决前置 左右相反的问题。所以调用下面这句就好了
        matrix.postScale(-1, 1);
        return Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
    }
```
<br>

### 2.7、设置闪光灯
```java
//切换闪光灯模式
    public void switchFlash(int flashMode) {
        if (!isSupportFlash() || cameraId == 1) {
            //设备不支持闪光灯模式，或者是前置摄像头
            return;
        }
        if (flashMode % 3 == 0) {
            //闪光灯自动
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        } else if (flashMode % 3 == 1) {
            //闪光灯开启
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
        } else if (flashMode % 3 == 2) {
            //闪光灯关闭
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }
```

### 2.8、切换前后置摄像头
前面我们说了 mCamera = Camera.open()，里可以带参数，0是后置，1是前置。那么我们给doOpenCamera(boolean isFront)方法加个前后置参数即可。
```java
//切换前后置摄像头
 //切换摄像头
    public void switchCamera() {
        isFront = !isFront;
        //关闭之前的摄像头，并释放
        CameraInterface.getInstance(getContext()).doStopCamera();
        //开启前置摄像头，其实这里也就是重新new
        CameraInterface.getInstance(getContext()).doOpenCamera(isFront);
        //开始预览
        CameraInterface.getInstance(getContext()).doStartPreview(mSurfaceHolder);
    }
```
<br>

### 2.9、我们的SurfaceView
其实上面说完了，SurfaceView很简单，直接贴代码。是的就是这么简单。当然我这里把其他多余代码先省略了。
```java
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    SurfaceHolder mSurfaceHolder;
    //是否是前置摄像头
    private boolean isFront;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSPARENT);
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInterface.getInstance(getContext()).doOpenCamera(isFront);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraInterface.getInstance(getContext()).doStartPreview(holder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraInterface.getInstance(getContext()).doStopCamera();
    }
}
```

<br>

### 2.10、看到这里，再结合我的dmeo,应该清楚了
效果图如下：

<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e7f192e824d64?w=481&h=852&f=gif&s=5222805"  height = "500" />闪光灯如下----><img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e7f4e7763c2fb?w=479&h=851&f=gif&s=4369214"  height = "500" />

[github地址](https://github.com/lihangleo2/MyCamera)


<br>

其实相机自带人脸识别api。但这个不是本章的重点，有兴趣的，可以去.
[一个人脸识别小demo](https://github.com/lihangleo2/Face-Recognition)
效果如下：
<img src = "https://user-gold-cdn.xitu.io/2020/3/17/170e7fc3fde537fd?w=481&h=850&f=gif&s=3359948"  height = "500" />


<br>

## 三、我的适配全面屏不变形的方案之一（有更好解决方案的请在评论区留言）
首先我们看一张图。

![](https://user-gold-cdn.xitu.io/2020/3/17/170e7ff04fb7dcd9?w=1080&h=2340&f=png&s=1544249)

这是我同事的一个小米10全面屏的。把我们的demo运行上去，图片上看不太清楚，还是有变形。这里我们分析下。
* 说明一： 得到最佳尺寸是 1920x1440
* 说明二： 手机尺寸是2250x1080
* 说明三： 如果是满屏。按照最佳尺寸计算可得出，相机应该的高度为 1920x1080/1440 = 1440。应该高度是1440，而实际高度是2250,远远不够，所以会有些拉伸，

从上得出，想要在全面屏手机上运行，必须设置SurfaceView的高度,在surfaceCreated，设置他的高度。
```java
@Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraInterface.getInstance(getContext()).doOpenCamera(isFront);

        //当前得到的最适当的尺寸
        int rightWith = CameraInterface.getInstance(getContext()).getRightHeight();
        int rightHeight = CameraInterface.getInstance(getContext()).getRightWith();

        //当前屏幕尺寸
        int phoneWith = UIUtil.getWidth(getContext());
        int phoneHeight = UIUtil.getHeight(getContext());
        int trueHeight = phoneWith * rightHeight / rightWith;

        //为了适配全面屏不变形做的处理
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
        layoutParams.width = phoneHeight;
        layoutParams.height = trueHeight;
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        setLayoutParams(layoutParams);
    }
```
可能这个方法比较粗糙，但是这是我目前能想到的最好的方法。如有更好的解决方法，请留下你的足迹。
