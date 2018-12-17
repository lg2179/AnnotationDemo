package com.hikvision.lg.compiler;

import com.google.auto.service.AutoService;
import com.hikvision.lg.annotation.BindView;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * <p>类说明<p>
 *
 * @author ligang14  2018/11/27
 * @version V1.0
 * @name BindViewProcessor
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {
    //返回用来报告错误、警报和其他通知的 Messager。
    private Messager mMessager;
    //用来创建文件
    private Filer mFiler;
    //返回用来在元素上进行操作的某些实用工具方法的实现。
    private Elements mElementUtils;
    private Map<String, ClassCreatorProxy> mProxyMap = new HashMap<>();
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mMessager = processingEnv.getMessager();
        mFiler = processingEnv.getFiler();
        mElementUtils = processingEnv.getElementUtils();
    }
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes=new LinkedHashSet<>();
        supportTypes.add(BindView.class.getCanonicalName());
        return supportTypes;
    }
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        mMessager.printMessage(Diagnostic.Kind.NOTE,"process  is  start");
        //因为process是多次的循环调用的，每次循环需要生成当前循环产生的所有代理文件，所以每次循环开始前需要将上次的代理类删除
        mProxyMap.clear();
        //获取所有注解
        Set<? extends Element> elements= roundEnv.getElementsAnnotatedWith(BindView.class);
        //遍历所有注解元素
        for (Element element: elements)
        {
            //因为我们知道我们编写的注解是用于全局变量控件上的，所以这里直接写成VariableElement
            VariableElement variableElement = (VariableElement) element;
            //因为变量元素拿不到类的全路径名称，所以我们先转成类元素
            TypeElement typeElement=(TypeElement) variableElement.getEnclosingElement();
            //通过类元素返回这个类的全路径名称
            String fullClassName=typeElement.getQualifiedName().toString();
            //查看当前集合中是否存在这个类全路径名称
            ClassCreatorProxy proxy=mProxyMap.get(fullClassName);
            //防止产生多个代理文件，因为多个注解可能都在同一个Activity中，只需要一个代理文件即可
            if (proxy==null)
            {
                //初始化代理类，通过代理类生成代理文件MainActivity_BindingView以及SecondActivity_BindingView
                proxy=new ClassCreatorProxy(mElementUtils,typeElement);
                mProxyMap.put(fullClassName,proxy);
            }
            BindView bindView=variableElement.getAnnotation(BindView.class);
            int id=bindView.value();
            proxy.putElement(id,variableElement);
        }
        for (String key:mProxyMap.keySet())
        {
            ClassCreatorProxy creatorProxy=mProxyMap.get(key);
            try {
                JavaFileObject fileObject=mFiler.createSourceFile(creatorProxy.getProxyClassFullName(),creatorProxy.getTypeElement());
                Writer writer=fileObject.openWriter();
                writer.write(creatorProxy.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
