///****************************************************************************/
///****************************************************************************/
///****     Copyright (C) 2013                                             ****/
///****     Antonio Manuel Rodrigues Manso                                 ****/
///****     e-mail: manso@ipt.pt                                           ****/
///****     url   : http://orion.ipt.pt/~manso                             ****/
///****     Instituto Politecnico de Tomar                                 ****/
///****     Escola Superior de Tecnologia de Tomar                         ****/
///****************************************************************************/
///****************************************************************************/
///****     This software was built with the purpose of investigating      ****/
///****     and learning. Its use is free and is not provided any          ****/
///****     guarantee or support.                                          ****/
///****     If you met bugs, please, report them to the author             ****/
///****                                                                    ****/
///****************************************************************************/
///****************************************************************************/
package currdig.utils;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

/**
 *
 * @author Zulu
 */
public class GuiUtils {
    static SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");

    /**
     * adiciona uma mensagem no formato "HH:mm:ss <Titulo> mensagem" no final da
     * caixa de texto com o titulo a Azul
     *
     * @param txt area de texto para apresentacao da mensagem
     * @param title titulo da mensagem
     * @param msg mensagem
     */
    public static void addText(final JTextPane txt, final String title, final String msg) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyleContext sc = new StyleContext();
                Style style = sc.getStyle(StyleContext.DEFAULT_STYLE);                
                Date now = new Date();
                String strDate = sdfDate.format(now);
                Document doc = txt.getDocument();
                StyleConstants.setForeground(style, Color.DARK_GRAY);
                doc.insertString(doc.getLength(), "\n" + strDate, style);
                StyleConstants.setForeground(style, Color.BLUE);
                doc.insertString(doc.getLength(), " " + title + " \t", style);
                StyleConstants.setForeground(style, Color.BLACK);
                doc.insertString(doc.getLength(), msg, style);
            } catch (BadLocationException ex) {
                Logger.getLogger(GuiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    /**
     * adiciona uma imagem no formato "HH:mm:ss <Titulo> imagem" no final da
     * caixa de texto com o titulo a Azul
     *
     * @param txt area de texto para apresentacao da mensagem
     * @param title titulo da mensagem
     * @param img imagem para ser apresentada
     */
    public static void addImage(final JTextPane txt, final String title, final ImageIcon img) {
        SwingUtilities.invokeLater(() -> {
            try {
                StyleContext sc = new StyleContext();
                Style style = sc.getStyle(StyleContext.DEFAULT_STYLE);
                Date now = new Date();
                String strDate = sdfDate.format(now);
                Document doc = txt.getDocument();
                StyleConstants.setForeground(style, Color.RED);
                doc.insertString(doc.getLength(), "\n" + strDate, style);
                StyleConstants.setForeground(style, Color.BLUE);
                doc.insertString(doc.getLength(), " " + title + "\n", style);
                StyleConstants.setIcon(style, img);
                doc.insertString(doc.getLength(), " ", style);
            } catch (BadLocationException ex) {
                Logger.getLogger(GuiUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

  
}
