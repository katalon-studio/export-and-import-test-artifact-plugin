package com.katalon.plugin.katashare.composer.util;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageUtil {

    public static Image loadImage(Bundle bundle, String imageURI) {
        URL url = FileLocator.find(bundle, new Path(imageURI), null);
        ImageDescriptor image = ImageDescriptor.createFromURL(url);
        return image.createImage();
    }
}
