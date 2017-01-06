package com.jaynewstrom.composite;

import com.google.auto.service.AutoService;
import com.jaynewstrom.json.runtime.AddToCompositeFactory;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public final class CompositeFactoryProcessor extends AbstractProcessor {
    private Filer filer;
    private Messager messager;
    private Elements elementUtils;
    private Types typeUtils;
    private Set<TypeName> serializerTypes;
    private Set<TypeName> deserializerTypes;

    @Override public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        serializerTypes = new LinkedHashSet<>();
        deserializerTypes = new LinkedHashSet<>();
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(AddToCompositeFactory.class.getCanonicalName());
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        int initialSerializerSize = serializerTypes.size();
        int initialDeserializerSize = deserializerTypes.size();
        for (Element element : roundEnv.getElementsAnnotatedWith(AddToCompositeFactory.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                error(element, "%s annotations can only be applied to classes!", AddToCompositeFactory.class.getSimpleName());
                return false;
            }
            TypeMirror elementType = element.asType();
            TypeName typeName = TypeName.get(elementType);
            boolean supportType = false;
            if (isType(elementType, JsonSerializer.class)) {
                serializerTypes.add(typeName);
                supportType = true;
            }
            if (isType(elementType, JsonDeserializer.class)) {
                deserializerTypes.add(typeName);
                supportType = true;
            }
            if (!supportType) {
                error(element, "%s is only supported on classes that implement %s or %s", AddToCompositeFactory.class.getSimpleName(),
                        JsonSerializer.class.getSimpleName(), JsonDeserializer.class.getSimpleName());
            }
        }
        if (shouldGenerateFactories(initialSerializerSize, serializerTypes) ||
                shouldGenerateFactories(initialDeserializerSize, deserializerTypes)) {
            writeFactory(serializerTypes, "Serializer", JsonSerializerFactory.class);
            writeFactory(deserializerTypes, "Deserializer", JsonDeserializerFactory.class);
        } else if (sizesChanged(initialSerializerSize, serializerTypes) || sizesChanged(initialDeserializerSize, deserializerTypes)) {
            messager.printMessage(Diagnostic.Kind.ERROR, "JsonCompositeFactories were already generated, but more types were found.");
        }
        return false;
    }

    private void writeFactory(Set<TypeName> types, String kind, Class<?> superClass) {
        TypeSpec.Builder builder = TypeSpec.classBuilder("CompositeJson" + kind + "Factory")
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(superClass);

        MethodSpec.Builder constructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);
        constructor.addStatement("super($L)", Math.round(types.size() * 1.4));
        for (TypeName type : types) {
            constructor.addStatement("register(new $T())", type);
        }
        builder.addMethod(constructor.build());

        JavaFile file = JavaFile.builder("com.jaynewstrom.json.runtime", builder.build()).build();
        try {
            file.writeTo(filer);
        } catch (IOException exception) {
            messager.printMessage(Diagnostic.Kind.ERROR, String.format("Failed to write file. \n%s", exception));
        }
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private boolean isType(TypeMirror elementType, Class<?> checkingForClass) {
        TypeElement jsonSerializerType = elementUtils.getTypeElement(checkingForClass.getCanonicalName());
        return typeUtils.isAssignable(elementType, typeUtils.erasure(jsonSerializerType.asType()));
    }

    private boolean shouldGenerateFactories(int initialSize, Collection<?> current) {
        return initialSize == 0 && current.size() > 0;
    }

    private boolean sizesChanged(int initialSize, Collection<?> current) {
        return initialSize != current.size();
    }
}
