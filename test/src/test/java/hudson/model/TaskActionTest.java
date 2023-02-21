package hudson.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import hudson.console.AnnotatedLargeText;
import hudson.security.ACL;
import hudson.security.Permission;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;


public class TaskActionTest {

    private static class MyTestTaskThread extends TaskThread {
        MyTestTaskThread(TaskAction taskAction) {
            super(taskAction, ListenerAndText.forMemory(taskAction));
        }

        @Override
        protected void perform(TaskListener listener) throws Exception {
            listener.hyperlink("/localpath", "a link");
        }
    }

    private static class MyTestTaskAction extends TaskAction {
        void start() {
            workerThread = new MyTestTaskThread(this);
            workerThread.start();
        }

        @Override
        public String getIconFileName() {
            return "Icon";
        }

        @Override
        public String getDisplayName() {
            return "My Task Thread";
        }

        @Override
        public String getUrlName() {
            return "xyz";
        }

        @Override
        protected Permission getPermission() {
            return Permission.READ;
        }

        @Override
        protected ACL getACL() {
            return ACL.lambda2((a, p) -> true);
        }
    }

    @Test
    public void annotatedText() throws Exception {
        MyTestTaskAction action = new MyTestTaskAction();
        action.start();
        AnnotatedLargeText annotatedText = action.obtainLog();
        String url = action.getSearchUrl();
        TaskThread taskThread = action.getWorkerThread();

        assertEquals(url, "xyz");
        assertEquals(taskThread.getName(), "My Task Thread");

        while (!annotatedText.isComplete()) {
            Thread.sleep(10);
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        final long length = annotatedText.writeLogTo(0, os);

        assertTrue("length should be longer or even 219", length >= 219);
        assertTrue(os.toString(StandardCharsets.UTF_8).startsWith("a linkCompleted"));
    }
}
