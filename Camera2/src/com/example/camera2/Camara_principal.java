package com.example.camera2;

import java.io.File;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

public class Camara_principal extends Activity implements CvCameraViewListener2{
	
	private CameraBridgeViewBase mCameraView;
	private Boolean tomarFoto;
	private Mat imagenOpenCV;
	private boolean camaraFrontal;
	private final int NUMERO_CAMARA=1;
	
    @SuppressWarnings("unused")
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(final int status) {
        	
            if(status == LoaderCallbackInterface.SUCCESS){
            	mCameraView.enableView();
				imagenOpenCV=new Mat();
            }
            else
                    super.onManagerConnected(status); 
            
        }
    };
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camara_principal);
		
		final Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		tomarFoto=false;
		CameraInfo cameraInfo=new CameraInfo();
		Camera.getCameraInfo(NUMERO_CAMARA, cameraInfo);
		camaraFrontal= cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK;
		mCameraView=new JavaCameraView (this, NUMERO_CAMARA);
		mCameraView.setCvCameraViewListener(this);
		
		setContentView(mCameraView);
	}
	
    @Override
    public void onPause()
    {
        super.onPause();
        if (mCameraView != null)
        	mCameraView.disableView();
    }
    
    @Override
    public void onResume()
    {
        super.onResume(); 
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
    }
    
    public void onDestroy() {
        super.onDestroy();
        if (mCameraView != null)
        	mCameraView.disableView();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camara_principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCameraViewStarted(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		final Mat rgba=inputFrame.rgba();
		final Mat imagenGris=new Mat();
		if(tomarFoto)
		{
			tomarFoto(rgba);
			tomarFoto=false;
			
			return rgba;
		}
		Imgproc.cvtColor(rgba, imagenGris, Imgproc.COLOR_RGBA2GRAY);
		return imagenGris;
	}


private void tomarFoto(final Mat rgba){
	final String ruta=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString()
			+File.separator+"Camara";
	File rutaImagen=new File(ruta);
	 
	if(!rutaImagen.isDirectory() && !rutaImagen.mkdirs()){
		Log.e("Camara", "Archivo no valido");
		return;
	}
	Imgproc.cvtColor(rgba, imagenOpenCV, Imgproc.COLOR_RGBA2BGR,3);
	if(!Imgcodecs.imwrite(ruta+File.separator+"imagen.bmp", imagenOpenCV)){
		Log.e("Camara", "Error al convertir imagen");
		return;
	}
	Log.v("com.example.camara", "Imagen guardada con exito");		
}
}
