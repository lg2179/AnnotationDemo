package com.hikvision.lg.compiler;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;

/**
 * <p>类说明<p>
 *
 * @author ligang14  2018/11/27
 * @version V1.0
 * @name ClassCreatorProxy
 */
public class ClassCreatorProxy {
    private String mBindingClassName;
    private String mPackageName;
    private TypeElement mTypeElement;
    private Map<Integer,VariableElement> mVariableElementMap =new HashMap<>();
    public ClassCreatorProxy(Elements elementUtils, TypeElement typeElement) {
        mTypeElement=typeElement;
        PackageElement packageElement=elementUtils.getPackageOf(mTypeElement);
        String packageName=packageElement.getQualifiedName().toString();
        String className=mTypeElement.getSimpleName().toString();
        mPackageName=packageName;
        mBindingClassName=className+"_ViewBinding";
    }
    public void putElement(int id,VariableElement element)
    {
        mVariableElementMap.put(id,element);
    }
    public String generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("package ").append(mPackageName).append(";\n\n");
        builder.append("import com.hikvison.lg.demo.*;\n");
        builder.append('\n');
        builder.append("public class ").append(mBindingClassName);
        builder.append(" {\n");
        generateMethods(builder);
        builder.append("}\n");
        return builder.toString();
    }
    private void generateMethods(StringBuilder builder) {
        builder.append("public void bind(" + mTypeElement.getQualifiedName() + " host ) {\n");
        for (int id : mVariableElementMap.keySet()) {
            VariableElement element = mVariableElementMap.get(id);
            String name = element.getSimpleName().toString();
            String type = element.asType().toString();
            builder.append("host." + name).append(" = ");
            builder.append("(" + type + ")(((android.app.Activity)host).findViewById( " + id + "));\n");
        }
        builder.append("  }\n");
    }

    public String getProxyClassFullName() {
        return mPackageName + "." + mBindingClassName;
    }

    public TypeElement getTypeElement() {
        return mTypeElement;
    }
}
