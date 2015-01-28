/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *  Copyright 2014 Connor Tumbleson <connor.tumbleson@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package brut.androlib;

import brut.androlib.res.util.ExtFile;
import brut.common.BrutException;
import brut.directory.DirectoryException;
import brut.util.OS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class SharedLibraryTest {

    @BeforeClass
    public static void beforeClass() throws BrutException {
        sTmpDir = new ExtFile(OS.createTempDirectory());
        TestUtils.copyResourceDir(SharedLibraryTest.class, "brut/apktool/shared_libraries/", sTmpDir);
    }

    @AfterClass
    public static void afterClass() throws BrutException {
        OS.rmdir(sTmpDir);
    }

    @Test
    public void isFrameworkTaggingWorking() throws AndrolibException {
        String apkName = "library.apk";

        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = sTmpDir.getAbsolutePath();
        apkOptions.frameworkTag = "building";

        new Androlib(apkOptions).installFramework(new File(sTmpDir + File.separator + apkName));

        assertTrue(fileExists("2-building.apk"));
    }

    @Test
    public void isFrameworkInstallingWorking() throws AndrolibException {
        String apkName = "library.apk";

        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = sTmpDir.getAbsolutePath();

        new Androlib(apkOptions).installFramework(new File(sTmpDir + File.separator + apkName));

        assertTrue(fileExists("2.apk"));
    }

    @Test
    public void isSharedResourceDecodingWorking() throws IOException, BrutException {
        String framework = "library.apk";
        String client = "client.apk";


        ApkOptions apkOptions = new ApkOptions();
        apkOptions.frameworkFolderLocation = sTmpDir.getAbsolutePath();
        apkOptions.frameworkTag = "shared";

        new Androlib(apkOptions).installFramework(new File(sTmpDir + File.separator + framework));

        assertTrue(fileExists("2-shared.apk"));

        ApkDecoder apkDecoder = new ApkDecoder(new File(sTmpDir + File.separator + client));
        apkDecoder.setOutDir(new File(sTmpDir + File.separator + client + ".out"));
        apkDecoder.setFrameworkDir(apkOptions.frameworkFolderLocation);
        apkDecoder.setFrameworkTag(apkOptions.frameworkTag);
        apkDecoder.decode();

        ExtFile testApk = new ExtFile(sTmpDir, client + ".out");
        new Androlib(apkOptions).build(testApk, null);

        assertTrue(fileExists(client + ".out" + File.separator + "dist" + File.separator + client));
    }

    private boolean fileExists(String filepath) {
        return Files.exists(Paths.get(sTmpDir.getAbsolutePath() + File.separator + filepath));
    }

    private static ExtFile sTmpDir;
}
