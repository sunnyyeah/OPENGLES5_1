package com.example.opengles5_1;
import static com.example.opengles5_1.ShaderUtil.createProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class SixPointedStar {
	int mProgram;		//�Զ�����Ⱦ������ɫ������id
	int muMVPMatrixHandle;		//�ܱ任��������
	int maPositionHandle;		//����λ����������
	int maColorHandle;			//������ɫ��������
	String mVertexShader;		//������ɫ������ű�
	String mFragmentShader;		//ƬԪ��ɫ������ű�
	static float[] mMMatrix = new float[16];		//���������3D�任���󣬰�����ת��ƽ�ơ�����
	
	FloatBuffer mVertexBuffer;	//�����������ݻ���
	FloatBuffer mColorBuffer;	//������ɫ���ݻ���
	int vCount = 0;
	float yAngle = 0;	//��y����ת�ĽǶ�
	float xAngle = 0;	//��x����ת�ĽǶ�
	final float UNIT_SIZE = 1;
	
	public SixPointedStar(MySurfaceView mv, float r, float R, float z){
		initVertexData(R,r,z);		//���ó�ʼ���������ݵ�initVertexData����
		initShader(mv);			//���ó�ʼ����ɫ����initShader����
	}

	private void initVertexData(float R, float r, float z) {
		List<Float> flist = new ArrayList<Float>();
		float tempAngle = 360 / 6;
		for(float angle = 0; angle < 360; angle += tempAngle){//ѭ�����ɹ��������Ǹ������εĶ�������
			//��һ�����x��y��z����
			flist.add(0f);	
			flist.add(0f);
			flist.add(z);
			//�ڶ������x��y��z����
			flist.add((float)(R * UNIT_SIZE * Math.cos(Math.toRadians(angle))));	
			flist.add((float)(R * UNIT_SIZE * Math.sin(Math.toRadians(angle))));
			flist.add(z);
			//���������x��y��z����
			flist.add((float)(r * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle / 2))));
			flist.add((float)(r * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle / 2))));
			flist.add(z);
			//��һ�����ĵ��x��y��z����
			flist.add(0f);
			flist.add(0f);
			flist.add(z);
			//�ڶ������x��y��z����
			flist.add((float)(r * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle / 2))));
			flist.add((float)(r * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle / 2))));
			flist.add(z);
			//���������x��y��z����
			flist.add((float)(R * UNIT_SIZE * Math.cos(Math.toRadians(angle + tempAngle))));
			flist.add((float)(R * UNIT_SIZE * Math.sin(Math.toRadians(angle + tempAngle))));
			flist.add(z);
		}
		vCount = flist.size() / 3;
		float[] vertexArray = new float[flist.size()];		//������������
		for(int i = 0;i < vCount; i++){
			vertexArray[i * 3] = flist.get(i * 3);
			vertexArray[i * 3 + 1] = flist.get(i * 3 + 1);
			vertexArray[i * 3 + 2] = flist.get(i * 3 + 2);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
		vbb.order(ByteOrder.nativeOrder());		//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
		mVertexBuffer = vbb.asFloatBuffer();
		mVertexBuffer.put(vertexArray);		//�������������ݷŽ�����
		mVertexBuffer.position(0);
		
		float[] colorArray = new float[vCount * 4];		//������ɫ���ݵĳ�ʼ��
		for(int i = 0; i < vCount; i++){
			if(i % 3 == 0){	//���ĵ�Ϊ��ɫ��RGBA 4��ͨ����1��1��1��0��
				colorArray[i * 4] = 1;
				colorArray[i * 4 + 1] = 1;
				colorArray[i * 4 + 2] = 1;
				colorArray[i * 4 + 3] = 0;
			}
			else{		//���ϵĵ�Ϊ����ɫ��RGBA 4��ͨ����0.45��0.75��0.75��0��
				colorArray[i * 4] = 0.45f;
				colorArray[i * 4 + 1] = 0.75f;
				colorArray[i * 4 + 2] = 0.75f;
				colorArray[i * 4 + 3] = 0;
			}
		}
		ByteBuffer cbb = ByteBuffer.allocateDirect(colorArray.length * 4);
		cbb.order(ByteOrder.nativeOrder());		//�����ֽ�˳��Ϊ���ز���ϵͳ˳��
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colorArray);		//��������ɫ���ݷŽ�����
		mColorBuffer.position(0);
	}

	private void initShader(MySurfaceView mv) {
		//���ض�����ɫ���Ľű�����
        mVertexShader=ShaderUtil.loadFromAssetsFile("vertex.sh", mv.getResources());
        //����ƬԪ��ɫ���Ľű�����
        mFragmentShader=ShaderUtil.loadFromAssetsFile("frag.sh", mv.getResources());  
        //���ڶ�����ɫ����ƬԪ��ɫ����������
        mProgram = createProgram(mVertexShader, mFragmentShader);
        //��ȡ�����ж���λ����������id  
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //��ȡ�����ж�����ɫ��������id  
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aColor");
        //��ȡ�������ܱ任��������id
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");  
	}


	public void drawSelf(){
		GLES20.glUseProgram(mProgram);		//ָ��ʹ��ĳ����ɫ������
		 //��ʼ���任����
        Matrix.setRotateM(mMMatrix,0,0,0,1,0);
        //������Z������λ��1
        Matrix.translateM(mMMatrix,0,0,0,1);
        //������y����ת
        Matrix.rotateM(mMMatrix,0,yAngle,0,1,0);
        //������z����ת
        Matrix.rotateM(mMMatrix,0,xAngle,1,0,0);  
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(mMMatrix), 0);	//�����ձ任��������Ⱦ����
		GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, mVertexBuffer);	//������λ�����ݴ��ͽ���Ⱦ����
		GLES20.glVertexAttribPointer(maColorHandle, 4, GLES20.GL_FLOAT, false, 4*4,mColorBuffer);		//����ɫ���ݴ��ͽ���Ⱦ����
		GLES20.glEnableVertexAttribArray(maPositionHandle);	//���ö���λ������
		GLES20.glEnableVertexAttribArray(maColorHandle);	//���ö�����ɫ����
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vCount);		//����������
	}
}
