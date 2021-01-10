package org.xm.core.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xm.core.File;
import org.xm.core.Folder;
import org.xm.core.INode;
import org.xm.core.Root;
import org.xm.core.exception.FileAlreadyExistsException;
import org.xm.core.exception.FolderAlreadyExistsException;
import org.xm.core.exception.UnsupportedOperationException;

public class TestTree {

    private static INode root;
    private static INode folderOne;
    private static INode folderTwo;
    private static INode fileOne;
    private static INode fileTwo;

    @BeforeClass
    public static void setUp() throws FileAlreadyExistsException, FolderAlreadyExistsException, UnsupportedOperationException {
        root = Root.getRootInstance("");
        folderOne = new Folder(root, "folder_001", 1);
        folderTwo = new Folder(root, "folder_002", 3);
        fileOne = new File(folderOne, "file_001", 4);
        fileTwo = new File(folderTwo, "file_002", 5);
    }

    @Test
    public void testTreeStructure() {
        assert root.children().size() == 2;
        assert folderOne.children().size() == 1;
        assert folderTwo.children().size() == 1;
        assert !root.isLeaf();
        assert root.getChild("folder_001") == folderOne;
        assert root.getChild("folder_002") == folderTwo;
        assert !folderTwo.isLeaf();
        assert !folderTwo.isLeaf();
        assert fileOne.isLeaf();
        assert fileTwo.isLeaf();
        assert folderOne.parent() == root;
        assert folderTwo.parent() == root;
        assert folderOne.getChild("file_001") == fileOne;
        assert folderTwo.getChild("file_002") == fileTwo;
    }

    @Test
    public void testAbsolutePath(){
        assert root.absolutePath().equals("");
        assert folderOne.absolutePath().equals("/folder_001");
        assert folderTwo.absolutePath().equals("/folder_002");
        assert fileOne.absolutePath().equals("/folder_001/file_001");
        assert fileTwo.absolutePath().equals("/folder_002/file_002");
    }

    @Test
    public void testAddSameFileFolder(){
        try {
            INode newFolderOne = new Folder(root, "folder_001", 100);
            Assert.fail("FolderAlreadyExistsException expected here");
        } catch (FileAlreadyExistsException | FolderAlreadyExistsException e) {
            if (e instanceof FileAlreadyExistsException)
                Assert.fail("FolderAlreadyExistsException expected here");
        }

        try {
            INode newFileOne = new File(folderOne, "file_001", 100);
            Assert.fail("FileAlreadyExistsException expected here");
        } catch (FileAlreadyExistsException | UnsupportedOperationException e) {
            if (e instanceof UnsupportedOperationException)
                Assert.fail("FileAlreadyExistsException expected here");
        }

    }

    @Test
    public void testAddChildToFile(){
        try {
            INode fileChild = new File(fileOne, "fileChild", 101);
            Assert.fail("UnsupportedOperationException expected here");
        } catch (FileAlreadyExistsException | UnsupportedOperationException e) {
            if (e instanceof FileAlreadyExistsException)
                Assert.fail("UnsupportedOperationException expected here");
        }
    }

}
