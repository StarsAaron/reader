package com.bysj.reader.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.Vector;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class MyBookPageFactory {
	private int mHeight, mWidth;//屏幕宽高
	private int mVisibleHeight, mVisibleWidth;
	private int mPageLineCount; //页面字体行数
	private int mLineSpace = 2; //行间距
	private int m_mpBufferLen; //文件长度
	private MappedByteBuffer m_mpBuff;
	private int m_mbBufEndPos = 0; //字符串结束位置
	private int m_mbBufBeginPos =0; //字符串开始位置
	private Paint mPaint; //画笔
	private int margingHeight = 30; //距离上边距离
	private int margingWeight = 30; //距离下边距离
	private int mFontSize = 30; //字体大小
	private Bitmap m_book_bg; //背景图像
    private String bgColor = "#FFFFFF";//默认背景颜色，白
	private Vector<String> m_lines = new Vector<String>(); //文件内容字符串（行为单位）
	
	public MyBookPageFactory(int w, int h, int fontsize,String fontColor){
		mWidth = w;
		mHeight = h;
		mFontSize = fontsize;
		mVisibleHeight = mHeight - margingHeight*2 - mFontSize;
		mVisibleWidth = mWidth -margingWeight*2;
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setTextSize(mFontSize);
		mPaint.setColor(Color.parseColor(fontColor));
		mPageLineCount = (int)mVisibleHeight/(mFontSize+2);
	}

    /**
     * 从阅读位置打开文件
     * @param path
     * @param position
     * @throws FileNotFoundException
     * @throws IOException
     */
	public void openBook(String path, int[] position) throws IOException{
		File file = new File(path);
		long length = file.length();
		m_mpBufferLen = (int)length;
		m_mpBuff = new RandomAccessFile(file, "r"). getChannel().map(FileChannel.MapMode.READ_ONLY, 0, length);
		m_mbBufEndPos = position[1];
		m_mbBufBeginPos = position[0];
	}

    /**
     * 绘制当前页面
     * @param canvas
     */
	public void onDrow(Canvas canvas){
		if(m_lines.size()==0){
			m_mbBufEndPos = m_mbBufBeginPos;
			m_lines = pageDown();
		}
		if(m_lines.size()>0){
			int y = margingHeight;
			if(m_book_bg != null){
				Rect rectF = new Rect(0, 0, mWidth, mHeight);
				canvas.drawBitmap(m_book_bg, null, rectF, null);
			}else{
                canvas.drawColor(Color.parseColor(bgColor));
			}
			for(String line : m_lines){
				y+=mFontSize+mLineSpace;
				canvas.drawText(line, margingWeight, y, mPaint);
			}
//			float persent = (float)m_mbBufBeginPos*100/m_mpBufferLen;
//			DecimalFormat strPersent  = new DecimalFormat("#0.00");
//			int strLen = (int)mPaint.measureText(strPersent.format(persent));
//			canvas.drawText(strPersent.format(persent) + "%", (mWidth-strLen)/2, mHeight-margingHeight, mPaint);
		}
		
	}

	/**
	 * 获取当前进度
	 * @return
     */
	public float getProgress(){
		return (float)m_mbBufBeginPos*100/m_mpBufferLen;
	}

	/**
	 * 获取文件长度
	 * @return
     */
	public float getMaxSize(){
		return m_mpBufferLen;
	}

	private void pageUp() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while((lines.size() < mPageLineCount) && (m_mbBufBeginPos > 0)){
			Vector<String> paraLines = new Vector<String>();

			byte[] parabuffer = readParagraphBack(m_mbBufBeginPos);
			
			m_mbBufBeginPos -= parabuffer.length;
			try {
				strParagraph = new String(parabuffer, "GBK");
//				Log.d("xjd", "strParagraph"+strParagraph);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "  ");
			strParagraph = strParagraph.replaceAll("\n", " ");

			while(strParagraph.length() > 0){
				int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);
				paraLines.add(strParagraph.substring(0, paintSize));
				strParagraph = strParagraph.substring(paintSize);
			}
			lines.addAll(0, paraLines);
			
			while (lines.size() > mPageLineCount) {
				try {
					m_mbBufBeginPos += lines.get(0).getBytes("GBK").length;
					lines.remove(0);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			m_mbBufEndPos = m_mbBufBeginPos;
		}
	}

    /**
     * 获取以行为单位的字符串
     * @return
     */
	private Vector<String> pageDown() {
		String strParagraph = "";
		Vector<String> lines = new Vector<String>();
		while((lines.size() < mPageLineCount) && (m_mbBufEndPos < m_mpBufferLen)){
			byte[] parabuffer = readParagraphForward(m_mbBufEndPos);//获取下一页的数据
			m_mbBufEndPos += parabuffer.length;//字符的尾位置
			try {
				strParagraph = new String(parabuffer, "GBK");
//				Log.d("xjd", strParagraph);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			strParagraph = strParagraph.replaceAll("\r\n", "  ");//把回车换行替换成空格
			strParagraph = strParagraph.replaceAll("\n", " ");
            //一个字符串太长需要换成几行显示
			while(strParagraph.length() > 0){
				int paintSize = mPaint.breakText(strParagraph, true, mVisibleWidth, null);//计算字符串在一个屏幕宽度能放的字符个数
				lines.add(strParagraph.substring(0, paintSize));
				strParagraph = strParagraph.substring(paintSize);
				if(lines.size() >= mPageLineCount){
					break;
				}
			}
            //如果字符串在当前页面放不下，需要放到下一页，修改当前页末尾的字符位置
			if(strParagraph.length()!=0){
				try {
					m_mbBufEndPos -= (strParagraph).getBytes("GBK").length;
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		
		return lines;
	}

    /**
     * 获取下一行的byte数据
     * @param m_mbBufPos 字符的开始位置
     * @return
     */
	private byte[] readParagraphForward(int m_mbBufPos) {
		byte b0 ;
		int i = m_mbBufPos;
		while(i < m_mpBufferLen){
			b0 = m_mpBuff.get(i++);
			if(b0 == 0x0a){//遇到换行符
				break;
			}
		}
		int nParaSize = i - m_mbBufPos;
		byte[] buf = new byte[nParaSize];
		for (i = 0; i < nParaSize; i++) {
			buf[i] = m_mpBuff.get(m_mbBufPos + i);
		}
		return buf;
	}

    /**
     * 获取上一行byte数据
     * @param m_mbBufBeginPos
     * @return
     */
	private byte[] readParagraphBack(int m_mbBufBeginPos) {
		byte b0;
		int i = m_mbBufBeginPos -1 ;
		while(i > 0){
			b0 = m_mpBuff.get(i);
			if(b0 == 0x0a && i != m_mbBufBeginPos -1 ){
				i++;
				break;
			}
			i--;
		}		
		int nParaSize = m_mbBufBeginPos -i ;
		byte[] buf = new byte[nParaSize];
		for (int j = 0; j < nParaSize; j++) {
			buf[j] = m_mpBuff.get(i + j);
		}
		return buf;
	}

    /**
     * 是否文件结尾
     * @return
     */
    public boolean isTheEnd(){
        return m_mbBufEndPos == m_mpBufferLen?true:false;
    }

    /**
     * 下一页
     */
	public void nextPage() {
		if(m_mbBufEndPos >= m_mpBufferLen){
			return;
		}else{
			m_lines.clear();
			m_mbBufBeginPos = m_mbBufEndPos;
			m_lines = pageDown();
		}
	}

    /**
     * 上一页
     */
	public void prePage() {
		if(m_mbBufBeginPos<=0){
			return;
		}
		m_lines.clear();
		pageUp();
		m_lines = pageDown();
	}

    /**
     * 获取当前阅读位置
     * @return
     */
	public int[] getPosition(){
		int[] a = new int[]{m_mbBufBeginPos, m_mbBufEndPos};
		return a;
	}

    /**
     * 获取当前页的最前面的文字
     * @return
     */
    public String getPresentDesc(){
        return m_lines.firstElement();
    }

    /**
     * 设置文字大小
     * @param fontsize
     */
	public void setTextFont(int fontsize) {
		mFontSize = fontsize;
		mPaint.setTextSize(mFontSize);
		mPageLineCount = mVisibleHeight/(mFontSize+mLineSpace);
		m_mbBufEndPos = m_mbBufBeginPos;
		nextPage();
	}

    /**
     * 获取文字大小
     * @return
     */
	public int getTextFont() {
		return mFontSize;
	}

    /**
     * 设置当前进度
     * @param persent
     */
	public void setPersent(int persent) {
		float a =  (float)(m_mpBufferLen*persent)/100;
		m_mbBufEndPos = (int)a;
		if(m_mbBufEndPos == 0){
			nextPage();
		}else{
			nextPage();
			prePage();
			nextPage();
		}
	}

    /**
     * 设置背景图片
     * @param BG
     */
	public void setBgBitmap(Bitmap BG) {
		m_book_bg = BG;
	}

    /**
     * 设置背景颜色字符串
     * @param color
     */
    public void setBgColor(String color){
        bgColor = color;
    }
}
