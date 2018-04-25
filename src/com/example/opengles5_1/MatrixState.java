package com.example.opengles5_1;


import android.opengl.Matrix;

public class MatrixState {
	private static float[] mProjMatrix = new float[16];		//4*4����ͶӰ��
	private static float[] mVMatrix = new float[16];		//�����λ�ó���9��������
	private static float[] mMVPMatrix;		//���յ��ܱ任����
	
	public static void setCamera(		//����������ķ���
								 int cx, int cy, float cz, 		//�����λ�õ�X��Y��Z����
								 int tx, int ty, float tz,		//�۲�Ŀ���X��Y��Z����
								 float upx, float upy, float upz) {	//up������X��Y��Z���ϵķ���
		Matrix.setLookAtM(
		        mVMatrix,      //�洢���ɾ���Ԫ�ص�float[]��������
		        0,             //�����ʼƫ����
		        cx, cy, cz,    //�����λ�õ�X��Y��Z����
		        tx, ty, tz,    //�۲�Ŀ���X��Y��Z����
		        upx, upy, upz  //up������X��Y��Z���ϵķ���
		);
	}

	public static void setProjectOrtho(	//��������ͶӰ�ķ���
			float left, float right,	//near���left��right
			int bottom, int top,		//near���bottom��top
			int near, int far) {				//near�桢far�����ӵ�ľ���
		Matrix.orthoM(
			    mProjMatrix,     //�洢���ɾ���Ԫ�ص�float[]��������
			    0,               //�����ʼƫ����
			    left, right,     //near���left��right
			    bottom, top,     //near���bottom��top
			    near, far        //near�桢far�����ӵ�ľ���
			);
	}

	public static float[] getFinalMatrix(float[] spec) {	//���������ܱ任����ķ���
		mMVPMatrix = new float[16];		//��������������ձ任���������
		Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, spec, 0);		//�������������Ա任����
		//��ͶӰ���������һ���Ľ������õ����ձ任����
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		return mMVPMatrix;
	}

	
}
