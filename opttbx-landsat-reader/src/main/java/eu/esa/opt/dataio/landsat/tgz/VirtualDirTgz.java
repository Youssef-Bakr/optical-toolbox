/*
 * Copyright (C) 2014 Brockmann Consult GmbH (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option)
 * any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see http://www.gnu.org/licenses/
 *//*


package eu.esa.opt.dataio.landsat.tgz;

import com.bc.ceres.core.VirtualDir;
import org.esa.snap.core.util.io.FileUtils;
import org.xeustechnologies.jtar.TarEntry;
import org.xeustechnologies.jtar.TarInputStream;
import ucar.unidata.io.bzip2.CBZip2InputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class VirtualDirTgz extends VirtualDir {

    private final File archiveFile;
    private File extractDir;

    public VirtualDirTgz(File tgz) throws IOException {
        if (tgz == null) {
            throw new IllegalArgumentException("Input file shall not be null");
        }
        archiveFile = tgz;
        extractDir = null;
    }

    @Override
    public String getBasePath() {
        return archiveFile.getPath();
    }

    @Override
    public File getBaseFile() {
        return archiveFile;
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        final File file = getFile(path);
        return new BufferedInputStream(new FileInputStream(file));
    }

    @Override
    public File getFile(String path) throws IOException {
        ensureUnpacked();
        final File file = new File(extractDir, path);
        if (!(file.isFile() || file.isDirectory())) {
            throw new IOException();
        }
        return file;
    }

    @Override
    public String[] list(String path) throws IOException {
        final File file = getFile(path);
        return file.list();
    }

    @Override
    public boolean exists(String path) {
        try {
            ensureUnpacked();
        } catch (IOException e) {
            return false;
        }
        final File file = new File(extractDir, path);
        return file.exists();
    }

    @Override
    public String[] listAllFiles() throws IOException {
        final TarInputStream tis = getTarInputStream();

        TarEntry entry;
        List<String> entryNames = new ArrayList<>();
        while ((entry = tis.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                entryNames.add(entry.getName());
            }
        }
        return entryNames.toArray(new String[entryNames.size()]);
    }

    @Override
    public void close() {
        if (extractDir != null) {
            FileUtils.deleteTree(extractDir);
            extractDir = null;
        }
    }

    @Override
    public boolean isCompressed() {
        return isTgz(archiveFile.getName()) || isTbz(archiveFile.getName());
    }

    @Override
    public boolean isArchive() {
        return true;
    }

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        close();
    }

    @Override
    public File getTempDir() throws IOException {
        return extractDir;
    }

    static String getFilenameFromPath(String path) {
        int lastSepIndex = path.lastIndexOf("/");
        if (lastSepIndex == -1) {
            lastSepIndex = path.lastIndexOf("\\");
            if (lastSepIndex == -1) {
                return path;
            }
        }

        return path.substring(lastSepIndex + 1, path.length());
    }

    static boolean isTgz(String filename) {
        String lcName = filename.toLowerCase();
        return lcName.endsWith(".tar.gz") || lcName.endsWith(".tgz");
    }

    static boolean isTbz(String filename) {
        String lcName = filename.toLowerCase();
        return lcName.endsWith(".tar.bz") || lcName.endsWith(".tbz") ||
               lcName.endsWith(".tar.bz2") || lcName.endsWith(".tbz2");
    }

    private void ensureUnpacked() throws IOException {
        if (extractDir == null) {
            extractDir = VirtualDir.createUniqueTempDir();

            try (TarInputStream tis = getTarInputStream()) {
                TarEntry entry;

                while ((entry = tis.getNextEntry()) != null) {
                    final String entryName = entry.getName();
                    if (entry.isDirectory()) {
                        final File directory = new File(extractDir, entryName);
                        ensureDirectory(directory);
                        continue;
                    }

                    final String fileNameFromPath = getFilenameFromPath(entryName);
                    final int pathIndex = entryName.indexOf(fileNameFromPath);
                    String tarPath = null;
                    if (pathIndex > 0) {
                        tarPath = entryName.substring(0, pathIndex - 1);
                    }

                    File targetDir;
                    if (tarPath != null) {
                        targetDir = new File(extractDir, tarPath);
                    } else {
                        targetDir = extractDir;
                    }

                    ensureDirectory(targetDir);
                    final File targetFile = new File(targetDir, fileNameFromPath);
                    if (targetFile.isFile()) {
                        continue;
                    }

                    if (!targetFile.createNewFile()) {
                        throw new IOException("Unable to create file: " + targetFile.getAbsolutePath());
                    }

                    try (OutputStream outStream = new BufferedOutputStream(new FileOutputStream(targetFile))) {
                        final byte data[] = new byte[1024 * 1024];
                        int count;
                        while ((count = tis.read(data)) != -1) {
                            outStream.write(data, 0, count);
                        }
                    }
                }
            }
        }
    }

    private TarInputStream getTarInputStream() throws IOException {
        TarInputStream tis;
        if (isTgz(archiveFile.getName())) {
            tis = new TarInputStream(
                        new GZIPInputStream(new BufferedInputStream(new FileInputStream(archiveFile))));
        } else if (isTbz(archiveFile.getName())) {
            BufferedInputStream bstream = new BufferedInputStream(new FileInputStream(archiveFile));
            tis = new TarInputStream(new CBZip2InputStream(bstream, true));
        } else {
            tis = new TarInputStream(new BufferedInputStream(new FileInputStream(archiveFile)));
        }
        return tis;
    }

    private void ensureDirectory(File targetDir) throws IOException {
        if (!targetDir.isDirectory()) {
            if (!targetDir.mkdirs()) {
                throw new IOException("unable to create directory: " + targetDir.getAbsolutePath());
            }
        }
    }
}
*/
