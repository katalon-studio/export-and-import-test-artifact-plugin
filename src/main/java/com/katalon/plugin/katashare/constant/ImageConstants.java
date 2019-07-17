package com.katalon.plugin.katashare.constant;

import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.katalon.plugin.katashare.composer.util.ImageUtil;

public class ImageConstants {

    public static Bundle currentBundle = FrameworkUtil.getBundle(ImageConstants.class);
    
    public static final Image IMG_16_ADD = ImageUtil.loadImage(currentBundle, "icons/add_16.png");

    public static final Image IMG_16_DELETE = ImageUtil.loadImage(currentBundle, "icons/delete_16.png");
}
