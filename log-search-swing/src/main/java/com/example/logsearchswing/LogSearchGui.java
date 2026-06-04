package com.example.logsearchswing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;

public class LogSearchGui {

    // ── Palette ──────────────────────────────────────────────────────────────
    private static final Color BG_DARK      = new Color(0x1E, 0x1E, 0x2E);
    private static final Color BG_PANEL     = new Color(0x28, 0x28, 0x3E);
    private static final Color BG_FIELD     = new Color(0x12, 0x12, 0x20);
    private static final Color ACCENT       = new Color(0x7C, 0xFC, 0xAA);
    private static final Color ACCENT_DIM   = new Color(0x3A, 0x7A, 0x55);
    private static final Color TEXT_PRIMARY = new Color(0xE8, 0xE8, 0xF0);
    private static final Color TEXT_MUTED   = new Color(0x88, 0x88, 0xAA);
    private static final Color BORDER_CLR   = new Color(0x40, 0x40, 0x60);
    private static final Color BTN_DANGER   = new Color(0xFF, 0x5F, 0x5F);

    // ── Fonts ────────────────────────────────────────────────────────────────
    private static final Font FONT_LABEL  = new Font("Segoe UI", Font.PLAIN,  12);
    private static final Font FONT_FIELD  = new Font("Consolas",  Font.PLAIN,  13);
    private static final Font FONT_RESULT = new Font("Consolas",  Font.PLAIN,  12);
    private static final Font FONT_TITLE  = new Font("Segoe UI",  Font.BOLD,   15);
    private static final Font FONT_BTN    = new Font("Segoe UI",  Font.BOLD,   12);

    // ── State ────────────────────────────────────────────────────────────────
    private final LogSearcher logSearcher;
    private String   currentFilePath;
    private int      currentContextLines;
    private String[] currentKeywords;
    private boolean  isAllMatch = true;   // default: All Matches ON

    // ── Components ───────────────────────────────────────────────────────────
    private JTextField filePathField;
    private JTextField keywordField;
    private JSpinner   contextSpinner;
    private JTextArea  resultArea;
    private JButton    searchButton;
    private JButton    nextSearchButton;
    private JButton    nextLinesButton;
    private JButton    clearButton;
    private JLabel     statusLabel;
    private JLabel     matchCountLabel;
    private JCheckBox  allMatchCheckbox;
    private int        totalMatches = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public LogSearchGui(LogSearcher logSearcher) {
        this.logSearcher = logSearcher;
        applyGlobalLAF();
        SwingUtilities.invokeLater(this::createAndShowGui);
    }

    // ── Look & Feel ───────────────────────────────────────────────────────────
    private void applyGlobalLAF() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("TextField.background",          BG_FIELD);
        UIManager.put("TextField.foreground",          TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",     ACCENT);
        UIManager.put("TextField.selectionBackground", ACCENT_DIM);
        UIManager.put("TextField.selectionForeground", TEXT_PRIMARY);
        UIManager.put("TextField.inactiveForeground",  TEXT_MUTED);
        UIManager.put("Spinner.background",            BG_FIELD);
        UIManager.put("Spinner.foreground",            TEXT_PRIMARY);
        UIManager.put("FormattedTextField.background", BG_FIELD);
        UIManager.put("FormattedTextField.foreground", TEXT_PRIMARY);
        UIManager.put("FormattedTextField.caretForeground", ACCENT);
        UIManager.put("ScrollPane.background",         BG_FIELD);
        UIManager.put("Viewport.background",           BG_FIELD);
        UIManager.put("Button.background",             BG_PANEL);
        UIManager.put("Button.foreground",             TEXT_PRIMARY);
        UIManager.put("Button.focus",                  new Color(0, 0, 0, 0));
        UIManager.put("CheckBox.background",           BG_PANEL);
        UIManager.put("CheckBox.foreground",           TEXT_PRIMARY);
        UIManager.put("ToolTip.background",            BG_PANEL);
        UIManager.put("ToolTip.foreground",            TEXT_PRIMARY);
        UIManager.put("ToolTip.border",                BorderFactory.createLineBorder(BORDER_CLR));
        UIManager.put("ToolTip.font",                  FONT_LABEL);
    }

    // ── Main window ──────────────────────────────────────────────────────────
    private void createAndShowGui() {
        JFrame frame = new JFrame("Log File Searcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setMinimumSize(new Dimension(700, 480));
        frame.getContentPane().setBackground(BG_DARK);
        frame.setLayout(new BorderLayout(0, 0));

        frame.add(buildHeader(),    BorderLayout.NORTH);
        frame.add(buildCenter(),    BorderLayout.CENTER);
        frame.add(buildStatusBar(), BorderLayout.SOUTH);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_PANEL);
        header.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, ACCENT_DIM),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        JLabel title = new JLabel("⚡ Log File Searcher");
        title.setFont(FONT_TITLE);
        title.setForeground(ACCENT);
        header.add(title, BorderLayout.WEST);

        matchCountLabel = new JLabel("Matches: 0");
        matchCountLabel.setFont(FONT_LABEL);
        matchCountLabel.setForeground(TEXT_MUTED);
        header.add(matchCountLabel, BorderLayout.EAST);

        return header;
    }

    // ── Center ────────────────────────────────────────────────────────────────
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 8));
        center.setBackground(BG_DARK);
        center.setBorder(BorderFactory.createEmptyBorder(12, 14, 8, 14));
        center.add(buildControlPanel(), BorderLayout.NORTH);
        center.add(buildResultPanel(),  BorderLayout.CENTER);
        return center;
    }

    // ── Controls ──────────────────────────────────────────────────────────────
    private JPanel buildControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_PANEL);
        panel.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        GridBagConstraints lc = gbc(0, 0, 0.0);
        GridBagConstraints fc = gbc(1, 0, 1.0);
        GridBagConstraints bc = gbc(2, 0, 0.0);

        // Row 0 – File path
        lc.gridy = 0; fc.gridy = 0; bc.gridy = 0;
        panel.add(label("Log File:"), lc);
        filePathField = styledField("Full path to your .log / .txt file");
        panel.add(filePathField, fc);
        JButton browseBtn = accentButton("Browse…");
        browseBtn.setToolTipText("Open file chooser");
        browseBtn.addActionListener(this::browseFile);
        bc.insets = new Insets(4, 6, 4, 0);
        panel.add(browseBtn, bc);

        // Row 1 – Keyword
        lc.gridy = 1; fc.gridy = 1; bc.gridy = 1;
        panel.add(label("Keyword(s):"), lc);
        keywordField = styledField("Comma-separated: ERROR,WARN,Exception");
        panel.add(keywordField, fc);
        JButton clearKwBtn = ghostButton("✕");
        clearKwBtn.setToolTipText("Clear keyword field");
        clearKwBtn.addActionListener(ev -> keywordField.setText(""));
        bc.insets = new Insets(4, 6, 4, 0);
        panel.add(clearKwBtn, bc);

        // Row 2 – Options + Buttons
        lc.gridy = 2; fc.gridy = 2; bc.gridy = 2;
        lc.insets = new Insets(8, 0, 0, 8);
        panel.add(label("Context Lines:"), lc);

        SpinnerNumberModel spinModel = new SpinnerNumberModel(20, 0, 20, 1);
        contextSpinner = new JSpinner(spinModel);
        contextSpinner.setFont(FONT_FIELD);
        styleSpinner(contextSpinner);

        JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        rowPanel.setBackground(BG_PANEL);
        rowPanel.add(contextSpinner);

        // ── All Matches Checkbox ──────────────────────────────────────────────
        allMatchCheckbox = new JCheckBox("All Matches");
        allMatchCheckbox.setSelected(true);          // default ON
        allMatchCheckbox.setFont(FONT_LABEL);
        allMatchCheckbox.setForeground(TEXT_PRIMARY);
        allMatchCheckbox.setBackground(BG_PANEL);
        allMatchCheckbox.setFocusPainted(false);
        allMatchCheckbox.setOpaque(true);
        allMatchCheckbox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        allMatchCheckbox.setToolTipText("✔ All Matches → show everything at once  |  ☐ → step one match at a time");
        allMatchCheckbox.addActionListener(ev -> {
            isAllMatch = allMatchCheckbox.isSelected();
            // Switch to single-match mode: enable Next Search only if data loaded
            nextSearchButton.setEnabled(!isAllMatch && currentFilePath != null);
            setStatus(isAllMatch
                ? "Mode: All Matches — press Search to find everything."
                : "Mode: Single Match — use ⟳ Next Search to step through.");
        });
        rowPanel.add(Box.createHorizontalStrut(8));
        rowPanel.add(allMatchCheckbox);
        rowPanel.add(Box.createHorizontalStrut(8));

        // ── Action Buttons ────────────────────────────────────────────────────
        searchButton     = accentButton("▶  Search");
        nextSearchButton = ghostButton("⟳  Next Search");
        nextLinesButton  = ghostButton("↓  Next Lines");
        clearButton      = dangerButton("⊘  Clear");

        nextSearchButton.setEnabled(false);
        nextLinesButton.setEnabled(false);

        searchButton    .addActionListener(this::startSearch);
        nextSearchButton.addActionListener(this::nextSearch);
        nextLinesButton .addActionListener(this::nextLines);
        clearButton     .addActionListener(ev -> clearResults());

        searchButton    .setToolTipText("Start new search from top of file");
        nextSearchButton.setToolTipText("Find next match (Single Match mode only)");
        nextLinesButton .setToolTipText("Show next N context lines after last match");
        clearButton     .setToolTipText("Clear results and fully reset state");

        rowPanel.add(searchButton);
        rowPanel.add(nextSearchButton);
        rowPanel.add(nextLinesButton);
        rowPanel.add(clearButton);

        fc.insets    = new Insets(8, 0, 0, 6);
        fc.gridwidth = 2;
        panel.add(rowPanel, fc);

        return panel;
    }

    // ── Result area ───────────────────────────────────────────────────────────
    private JPanel buildResultPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG_DARK);

        JLabel resultTitle = new JLabel("  Results");
        resultTitle.setFont(FONT_LABEL);
        resultTitle.setForeground(TEXT_MUTED);
        resultTitle.setBorder(BorderFactory.createEmptyBorder(0, 2, 4, 0));
        wrapper.add(resultTitle, BorderLayout.NORTH);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(FONT_RESULT);
        resultArea.setBackground(BG_FIELD);
        resultArea.setForeground(TEXT_PRIMARY);
        resultArea.setCaretColor(ACCENT);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        resultArea.setSelectionColor(ACCENT_DIM);

        JScrollPane scroll = new JScrollPane(resultArea);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
        scroll.getViewport().setBackground(BG_FIELD);
        scroll.getVerticalScrollBar().setBackground(BG_PANEL);
        scroll.getHorizontalScrollBar().setBackground(BG_PANEL);

        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Status bar ────────────────────────────────────────────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BG_PANEL);
        bar.setBorder(new CompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER_CLR),
            BorderFactory.createEmptyBorder(5, 14, 5, 14)));

        statusLabel = new JLabel("Ready — open a log file to begin.");
        statusLabel.setFont(FONT_LABEL);
        statusLabel.setForeground(TEXT_MUTED);
        bar.add(statusLabel, BorderLayout.WEST);

        JLabel hint = new JLabel("Tip: comma-separated keywords for multi-search");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(0x55, 0x55, 0x77));
        bar.add(hint, BorderLayout.EAST);

        return bar;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void browseFile(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fc.setDialogTitle("Select Log File");
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fc.getSelectedFile().getAbsolutePath());
            setStatus("File selected: " + fc.getSelectedFile().getName());
        }
    }

    private void startSearch(ActionEvent e) {
        String filePath = filePathField.getText().trim();
        String keyword  = keywordField.getText().trim();

        if (filePath.isEmpty()) {
            setStatus("⚠ Please provide a log file path.");
            shake(filePathField);
            return;
        }
        if (keyword.isEmpty()) {
            setStatus("⚠ Please enter at least one keyword.");
            shake(keywordField);
            return;
        }

        currentContextLines = (int) contextSpinner.getValue();
        currentFilePath     = filePath;
        currentKeywords     = keyword.split(",");

        // ── KEY FIX: Always fully reset LogSearcher before a new search ───────
        logSearcher.reset();
        logSearcher.loadData(currentFilePath);

        totalMatches = 0;
        resultArea.setText("");
        matchCountLabel.setText("Matches: 0");
        nextSearchButton.setEnabled(!isAllMatch);
        nextLinesButton.setEnabled(false);

        setStatus("Searching for: " + keyword + " …");
        nextSearch(e);
    }

    private void nextSearch(ActionEvent e) {
        List<String> results;

        if (isAllMatch) {
            results = logSearcher.findAllMatch(currentKeywords);
        } else {
            results = new ArrayList<>();
            String match = logSearcher.findNextMatch(currentKeywords);
            if (match != null) {
                results.add(match);
            }
        }

        long hits = results.stream()
            .filter(r -> r.startsWith("Match at Line"))
            .count();
        totalMatches += hits;
        matchCountLabel.setText("Matches: " + totalMatches);
        appendResults(results);

        boolean hasRealMatch = hits > 0;
        nextLinesButton.setEnabled(hasRealMatch);

        if (!hasRealMatch) {
            nextSearchButton.setEnabled(false);
            nextLinesButton.setEnabled(false);
            setStatus("Search complete — no more matches.");
        } else {
            nextSearchButton.setEnabled(!isAllMatch);
            setStatus("Found " + hits + " match(es) in this pass.");
        }
    }

    private void nextLines(ActionEvent e) {
        List<String> results = logSearcher.getNextLines(currentKeywords);
        appendResults(results);

        if (results.isEmpty()
                || results.get(0).startsWith("No more lines")
                || results.get(0).startsWith("Error")
                || results.get(0).startsWith("No file")) {
            nextSearchButton.setEnabled(false);
            nextLinesButton.setEnabled(false);
            setStatus("No more lines available.");
        } else if (results.get(0).startsWith("Stopped at next match")) {
            nextLinesButton.setEnabled(false);
            setStatus("Stopped — next keyword match encountered.");
        } else {
            setStatus("Showing " + results.size() + " context line(s).");
        }
    }

    /**
     * Clear button:
     * - UI reset (results, counters, buttons)
     * - LogSearcher.reset() — keywordsLength, currkeywords, currentLineNumber 0/null
     * - clear → uncheck → search → Next Search correctly work
     */
    private void clearResults() {
        resultArea.setText("");
        totalMatches = 0;
        matchCountLabel.setText("Matches: 0");

        // Full LogSearcher state reset — restart app
        logSearcher.reset();
        currentFilePath = null;

        nextSearchButton.setEnabled(false);
        nextLinesButton.setEnabled(false);
        setStatus("Results cleared — ready for a new search.");
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void appendResults(List<String> lines) {
        for (String line : lines) resultArea.append(line + "\n");
        resultArea.setCaretPosition(resultArea.getDocument().getLength());
    }

    private void setStatus(String msg) { statusLabel.setText(msg); }

    private void shake(JComponent comp) {
        Point origin = comp.getLocation();
        Timer t = new Timer(30, null);
        int[] step = {0};
        int[] offsets = {-6, 6, -4, 4, -2, 2, 0};
        t.addActionListener(ev -> {
            if (step[0] < offsets.length) {
                comp.setLocation(origin.x + offsets[step[0]++], origin.y);
            } else {
                comp.setLocation(origin);
                t.stop();
            }
        });
        t.start();
    }

    // ── Widget factory methods ────────────────────────────────────────────────

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_LABEL);
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 10));
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField(28) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !isFocusOwner()) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(new Color(0x55, 0x55, 0x77));
                    g2.setFont(getFont().deriveFont(Font.ITALIC));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left + 2,
                                  ins.top + g2.getFontMetrics().getAscent());
                }
            }
        };
        f.setFont(FONT_FIELD);
        f.setOpaque(true);
        f.setBackground(BG_FIELD);
        f.setForeground(TEXT_PRIMARY);
        f.setDisabledTextColor(TEXT_MUTED);
        f.setCaretColor(ACCENT);
        f.setSelectionColor(ACCENT_DIM);
        f.setSelectedTextColor(TEXT_PRIMARY);
        f.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        f.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(ACCENT_DIM, 1),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            }
            @Override public void focusLost(FocusEvent e) {
                f.setBorder(new CompoundBorder(
                    BorderFactory.createLineBorder(BORDER_CLR),
                    BorderFactory.createEmptyBorder(6, 8, 6, 8)));
            }
        });
        return f;
    }

    private void styleSpinner(JSpinner s) {
        s.setPreferredSize(new Dimension(70, 32));
        JComponent editor = s.getEditor();
        if (editor instanceof JSpinner.DefaultEditor de) {
            JTextField tf = de.getTextField();
            tf.setOpaque(true);
            tf.setBackground(BG_FIELD);
            tf.setForeground(TEXT_PRIMARY);
            tf.setCaretColor(ACCENT);
            tf.setSelectionColor(ACCENT_DIM);
            tf.setSelectedTextColor(TEXT_PRIMARY);
            tf.setFont(FONT_FIELD);
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        }
        s.setBorder(BorderFactory.createLineBorder(BORDER_CLR));
    }

    private JButton accentButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(ACCENT);
        b.setForeground(new Color(0x0A, 0x1A, 0x12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        b.addMouseListener(hoverDarken(b, ACCENT, ACCENT_DIM.brighter()));
        return b;
    }

    private JButton ghostButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(BG_PANEL);
        b.setForeground(TEXT_PRIMARY);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(BORDER_CLR),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(hoverDarken(b, BG_PANEL, new Color(0x35, 0x35, 0x52)));
        return b;
    }

    private JButton dangerButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BTN);
        b.setBackground(BG_PANEL);
        b.setForeground(BTN_DANGER);
        b.setFocusPainted(false);
        b.setBorder(new CompoundBorder(
            BorderFactory.createLineBorder(new Color(0x80, 0x30, 0x30)),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(hoverDarken(b, BG_PANEL, new Color(0x40, 0x18, 0x18)));
        return b;
    }

    private MouseAdapter hoverDarken(JButton btn, Color normal, Color hover) {
        return new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (btn.isEnabled()) btn.setBackground(hover);
            }
            @Override public void mouseExited(MouseEvent e) {
                btn.setBackground(normal);
            }
        };
    }

    private GridBagConstraints gbc(int x, int y, double weightx) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = x; c.gridy = y;
        c.weightx = weightx;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(4, 0, 4, x == 1 ? 6 : 0);
        c.anchor  = GridBagConstraints.WEST;
        return c;
    }
}