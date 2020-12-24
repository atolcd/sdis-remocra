package fr.sdis83.remocra.http;

import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.ReadableByteChannel;

// Implementation mostly copied from ResourceCollection
class ClassLoaderResource extends Resource {
@Override
public boolean isContainedIn(Resource r) throws MalformedURLException {
        return false;
}

@Override
public void close() {
        // no-op
}

@Override
public boolean exists() {
        return true;
}

@Override
public boolean isDirectory() {
        return true;
}

@Override
public long lastModified() {
        return -1;
}

@Override
public long length() {
        return -1;
}

@Override
@Deprecated
public URL getURL() {
        return null;
}

@Override
public File getFile() throws IOException {
        return null;
}

@Override
public String getName() {
        return null;
}

@Override
public InputStream getInputStream() throws IOException {
        return null;
}

@Override
public ReadableByteChannel getReadableByteChannel() throws IOException {
        return null;
}

@Override
public boolean delete() throws SecurityException {
        throw new UnsupportedOperationException();
}

@Override
public boolean renameTo(Resource dest) throws SecurityException {
        throw new UnsupportedOperationException();
}

@Override
public String[] list() {
        throw new UnsupportedOperationException();
}

@Override
public Resource addPath(String path) throws IOException, MalformedURLException {
        path = URIUtil.canonicalPath(path);
        if (path == null) {
                throw new MalformedURLException();
        }

        if (path.length() == 0 || URIUtil.SLASH.equals(path)) {
                return this;
        }

        return Resource.newClassPathResource(path);
}
}
