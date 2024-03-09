package com.example.datainsert.exagear.controlsV2.widget;

import android.content.res.XmlResourceParser;
import android.util.AttributeSet;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

/**
 * 继承XmlResourceParser，接收一个XmlResourceParser实例，全部函数都调用它的函数，除了close的时候会把实例置为null
 * <br/> 主要解决AttributeSet接口导致的优先从resource读取attr而非xml自带的attr，导致视图的app属性不对
 * <br/> 好吧内部有强转XmlBlock.Parser，那只能写个Stub然后继承这个了。也不行，这个Parser是final！
 */
public class CustomXmlResourceParser implements XmlResourceParser {
    private XmlResourceParser mParser;
    public CustomXmlResourceParser(XmlResourceParser inner){
        mParser = inner;
    }
    @Override
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        mParser.setFeature(name,state);
    }

    @Override
    public boolean getFeature(String name) {
        return mParser.getFeature(name);
    }

    @Override
    public void setProperty(String name, Object value) throws XmlPullParserException {
        mParser.setProperty(name,value);
    }

    @Override
    public Object getProperty(String name) {
        return mParser.getProperty(name);
    }

    @Override
    public void setInput(Reader in) throws XmlPullParserException {
        mParser.setInput(in);
    }

    @Override
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        mParser.setInput(inputStream,inputEncoding);
    }

    @Override
    public String getInputEncoding() {
        return mParser.getInputEncoding();
    }

    @Override
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        mParser.defineEntityReplacementText(entityName,replacementText);
    }

    @Override
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return mParser.getNamespaceCount(depth);
    }

    @Override
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return mParser.getNamespacePrefix(pos);
    }

    @Override
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return mParser.getNamespaceUri(pos);
    }

    @Override
    public String getNamespace(String prefix) {
        return mParser.getNamespace(prefix);
    }

    @Override
    public int getDepth() {
        return mParser.getDepth();
    }

    @Override
    public String getPositionDescription() {
        return mParser.getPositionDescription();
    }

    @Override
    public int getLineNumber() {
        return mParser.getLineNumber();
    }

    @Override
    public int getColumnNumber() {
        return mParser.getColumnNumber();
    }

    @Override
    public boolean isWhitespace() throws XmlPullParserException {
        return mParser.isWhitespace();
    }

    @Override
    public String getText() {
        return mParser.getText();
    }

    @Override
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return mParser.getTextCharacters(holderForStartAndLength);
    }

    @Override
    public String getNamespace() {
        return mParser.getNamespace();
    }

    @Override
    public String getName() {
        return mParser.getName();
    }

    @Override
    public String getPrefix() {
        return mParser.getPrefix();
    }

    @Override
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return mParser.isEmptyElementTag();
    }

    @Override
    public int getAttributeCount() {
        return mParser.getAttributeCount();
    }

    @Override
    public String getAttributeNamespace(int index) {
        return mParser.getAttributeNamespace(index);
    }

    @Override
    public String getAttributeName(int index) {
        return mParser.getAttributeName(index);
    }

    @Override
    public String getAttributePrefix(int index) {
        return mParser.getAttributePrefix(index);
    }

    @Override
    public String getAttributeType(int index) {
        return mParser.getAttributeType(index);
    }

    @Override
    public boolean isAttributeDefault(int index) {
        return mParser.isAttributeDefault(index);
    }

    @Override
    public String getAttributeValue(int index) {
        return mParser.getAttributeValue(index);
    }

    @Override
    public String getAttributeValue(String namespace, String name) {
        return mParser.getAttributeValue(namespace, name);
    }

    @Override
    public int getEventType() throws XmlPullParserException {
        return mParser.getEventType();
    }

    @Override
    public int next() throws IOException, XmlPullParserException {
        return mParser.next();
    }

    @Override
    public int nextToken() throws IOException, XmlPullParserException {
        return mParser.nextToken();
    }

    @Override
    public void require(int type, String namespace, String name) throws IOException, XmlPullParserException {
        mParser.require(type, namespace, name);
    }

    @Override
    public String nextText() throws IOException, XmlPullParserException {
        return mParser.nextText();
    }

    @Override
    public int nextTag() throws IOException, XmlPullParserException {
        return mParser.nextTag();
    }

    @Override
    public int getAttributeNameResource(int index) {
        return mParser.getAttributeNameResource(index);
    }

    @Override
    public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
        return mParser.getAttributeListValue(namespace,attribute,options,defaultValue);
    }

    @Override
    public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
        return mParser.getAttributeBooleanValue(namespace,attribute,defaultValue);
    }

    @Override
    public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
        if(namespace.equals("app"))
            return defaultValue;
        return mParser.getAttributeResourceValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
        return mParser.getAttributeIntValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
        return mParser.getAttributeUnsignedIntValue(namespace, attribute, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
        return mParser.getAttributeFloatValue(namespace, attribute, defaultValue);
    }

    @Override
    public int getAttributeListValue(int index, String[] options, int defaultValue) {
        return mParser.getAttributeListValue(index, options, defaultValue);
    }

    @Override
    public boolean getAttributeBooleanValue(int index, boolean defaultValue) {
        return mParser.getAttributeBooleanValue(index, defaultValue);
    }

    @Override
    public int getAttributeResourceValue(int index, int defaultValue) {
        return defaultValue;
//        return mParser.getAttributeResourceValue(index,defaultValue);
    }

    @Override
    public int getAttributeIntValue(int index, int defaultValue) {
        return mParser.getAttributeIntValue(index,defaultValue);
    }

    @Override
    public int getAttributeUnsignedIntValue(int index, int defaultValue) {
        return mParser.getAttributeUnsignedIntValue(index, defaultValue);
    }

    @Override
    public float getAttributeFloatValue(int index, float defaultValue) {
        return mParser.getAttributeFloatValue(index, defaultValue);
    }

    @Override
    public String getIdAttribute() {
        return mParser.getIdAttribute();
    }

    @Override
    public String getClassAttribute() {
        return mParser.getClassAttribute();
    }

    @Override
    public int getIdAttributeResourceValue(int defaultValue) {
        return mParser.getIdAttributeResourceValue(defaultValue);
    }

    @Override
    public int getStyleAttribute() {
        return mParser.getStyleAttribute();
    }

    @Override
    public void close() {
        mParser.close();
        mParser=null;
    }
}
