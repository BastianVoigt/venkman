package venkman;

import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import venkman.swingex.ActionLambda;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.UncheckedIOException;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class MainWindow {
    private final JFrame frame;
    private JTextArea responseView;

    MainWindow() {
        this.frame = new JFrame("Venkman");
    }

    private void changeFontSize(double factor) {
        Container contentPane = frame.getContentPane();
        Font font = contentPane.getFont();
        float fontSizePt = font.getSize();
        float increasedFontSizePt = (int) (fontSizePt * factor);
        if (increasedFontSizePt == fontSizePt) {
            ++increasedFontSizePt;
        }
        Font increasedFont = font.deriveFont(increasedFontSizePt);
        setFontRecursive(contentPane, increasedFont);
        frame.pack();
    }

    private static void setFontRecursive(Component c, Font font) {
        if (Container.class.isAssignableFrom(c.getClass())) {
            Component[] children = ((Container) c).getComponents();
            for (Component child : children) {
                setFontRecursive(child, font);
            }
        }
        c.setFont(font);
    }

    void init() {
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        Container contentPane = frame.getContentPane();
        contentPane.add(createNavBar(), BorderLayout.NORTH);
        contentPane.add(createResponseView(), BorderLayout.CENTER);
        JRootPane rootPane = frame.getRootPane();
        registerKeyBinding(rootPane, KeyEvent.VK_PLUS, InputEvent.CTRL_MASK, "Increase Font Size", e -> changeFontSize(1.2D));
        registerKeyBinding(rootPane, KeyEvent.VK_MINUS, InputEvent.CTRL_MASK, "Decrease Font Size", e -> changeFontSize(1D / 1.2D));
        frame.pack();
        frame.setVisible(true);
    }

    private void registerKeyBinding(JRootPane rootPane, int keyEvent, int modifier, String actionName, ActionLambda actionListener) {
        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyEvent, modifier);
        rootPane.getInputMap().put(keyStroke, actionName);
        rootPane.getActionMap().put(actionName, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionListener.actionPerformed(e);
            }
        });
    }

    private JPanel createNavBar() {
        JComboBox methodSelector = new JComboBox(new Object[]{
                "GET",
                "POST",
                "PUT",
                "HEAD",
                "DELETE"
        });
        JTextField urlInput = new JTextField();
        urlInput.setText("http://blablabla.de:8080/main/entpunkt");
        JButton sendButton = new JButton(new SendAction(methodSelector, urlInput));
        GridBagLayout layout = new GridBagLayout();
        JPanel navBar = new JPanel(layout);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        constraints.weightx = 0;
        navBar.add(methodSelector, constraints);
        constraints.gridx = 1;
        constraints.weightx = 2;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        navBar.add(urlInput, constraints);
        constraints.weightx = 0;
        constraints.gridx = 2;
        constraints.fill = GridBagConstraints.NONE;
        navBar.add(sendButton, constraints);
        return navBar;
    }

    private HttpUriRequest createRequest(String method, String url) {
        switch(method) {
            case "GET":
                return new HttpGet(url);
            case "POST":
                return new HttpPost(url);
            case "PUT":
                return new HttpPut(url);
            case "HEAD":
                return new HttpHead(url);
            case "DELETE":
                return new HttpDelete(url);
            default:
                throw new IllegalArgumentException("Invalid method "+method);
        }
    }

    private JComponent createResponseView() {
        responseView = new JTextArea();
        responseView.setRows(20);
        responseView.setText("Hier steht dann die Response");
        return new JScrollPane(responseView);
    }

    private class SendAction extends AbstractAction {
        private final JComboBox methodSelector;
        private final JTextField urlInput;

        public SendAction(JComboBox methodSelector, JTextField urlInput) {
            super("Send");
            this.methodSelector = methodSelector;
            this.urlInput = urlInput;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            responseView.setText("");
            try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                    .disableRedirectHandling()
                    .build()) {
                HttpUriRequest request = createRequest((String) methodSelector.getSelectedItem(), urlInput.getText());
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    String responseString = EntityUtils.toString(response.getEntity());
                    responseView.setText(responseString);
                }
            } catch (IOException e1) {
                throw new UncheckedIOException(e1);
            }
        }
    }
}
