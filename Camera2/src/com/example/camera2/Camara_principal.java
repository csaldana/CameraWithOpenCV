package com.example.camera2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

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
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class Camara_principal extends Activity implements CvCameraViewListener2{
	
	private CameraBridgeViewBase mCameraView;
	private Boolean tomarFoto;
	
	private Mat imagenOpenCV;
	private boolean camaraFrontal;
	private boolean cambiarCamera, cambiarColor, bandera;
	private  int NUMERO_CAMARA=0;
	//resolucion de la camara

	
	
	
	
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
		cambiarCamera = false;
		cambiarColor = false;
		bandera = false;
		
		CameraInfo cameraInfo=new CameraInfo();
		Camera.getCameraInfo(NUMERO_CAMARA, cameraInfo);
		camaraFrontal= cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
		mCameraView=new JavaCameraView (this, NUMERO_CAMARA);
		mCameraView.setCameraIndex(NUMERO_CAMARA); //Tomar id de la camara
		mCameraView.setCvCameraViewListener(this);
		setContentView(mCameraView);
	}
	
	public void camaras(){
		
		if(cambiarCamera == false){
			cambiarCamera = true;
			NUMERO_CAMARA = 1;
			
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(NUMERO_CAMARA, cameraInfo);
			camaraFrontal= cameraInfo.facing == CameraInfo.CAMERA_FACING_FRONT;
			//mCameraView=new JavaCameraView (this, NUMERO_CAMARA);
			mCameraView.setCameraIndex(NUMERO_CAMARA); //Cambiar id de la camara
			setContentView(mCameraView);
			
			Toast.makeText(getApplicationContext(), "Estoy en camara frontal",Toast.LENGTH_LONG).show();
		}
		else{
			
			cambiarCamera = false;
			NUMERO_CAMARA = 0;
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(NUMERO_CAMARA, cameraInfo);
			camaraFrontal= cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK;
			//mCameraView=new JavaCameraView (this, NUMERO_CAMARA);
			mCameraView.setCameraIndex(NUMERO_CAMARA); // Cambiar id de la camara
			setContentView(mCameraView);
			
			Toast.makeText(getApplicationContext(), "Estoy en camara trasera",Toast.LENGTH_LONG).show();
				
			}
			
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
		switch(id){
		case  R.id.action_settings:
			return true;
		case R.id.tomar_foto:
			tomarFoto = true;
		break;
		case R.id.cambiarCamera:
			camaras();
		break;
		case R.id.cambiarColor:
			cambiarColor = bandera;
		break;
		case R.id.cambiarResolucion:

		break;
			
		}
		return true;//super.onOptionsItemSelected(item);
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
		
		if(cambiarColor == false){//Imagen a color
			bandera = true;
			if(tomarFoto == true){
				tomarFoto(rgba);
				tomarFoto=false;
				return rgba;
			}
		
			Imgproc.cvtColor(rgba, imagenGris, Imgproc.COLOR_RGBA2RGB);
			return rgba;
		}
		else{// Imagen a gris
			
			bandera = false;
			if(tomarFoto == true){
				tomarFotoGris(rgba);
				tomarFoto=false;
				return rgba;
			}
			Imgproc.cvtColor(rgba, imagenGris,Imgproc.COLOR_RGB2GRAY);
			return imagenGris;
			
		}
		
	}


	private void tomarFoto( final Mat rgba ){
		
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimePicture = sdf.format(new Date());
        String fileName = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
		File rutaImagen=new File(fileName);
		 
		if(!rutaImagen.isDirectory() && !rutaImagen.mkdirs()){
			Log.e("Camara", "Archivo no valido");
			return;
		}
		Imgproc.cvtColor(rgba, imagenOpenCV, Imgproc.COLOR_RGBA2BGR,3);
		if(!Imgcodecs.imwrite(fileName+File.separator+"imagen"+ dateTimePicture +".bmp", imagenOpenCV)){
			Log.e("Camara", "Error al convertir imagen");
			return;
		}
		Log.v("com.example.camara", "Imagen guardada con exito");		
	}
	
	private void tomarFotoGris( final Mat imagenGris ){
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String dateTimePicture = sdf.format(new Date());
        String ruta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
		File rutaImagen=new File(ruta);
		 
		if(!rutaImagen.isDirectory() && !rutaImagen.mkdirs()){
			Log.e("Camara", "Archivo no valido");
			return;
		}
		Imgproc.cvtColor(imagenGris, imagenOpenCV, Imgproc.COLOR_RGB2GRAY,3);
		if(!Imgcodecs.imwrite(ruta+File.separator+"imagen"+ dateTimePicture + ".bmp", imagenOpenCV)){
			Log.e("Camara", "Error al convertir imagen");
			return;
		}
		Log.v("com.example.camara", "Imagen guardada con exito");		
	}
	
	}
