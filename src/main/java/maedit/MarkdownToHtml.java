package maedit;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.options.MutableDataSet;

public class MarkdownToHtml {
    Parser parser;
    HtmlRenderer renderer;
    
    public MarkdownToHtml() {
        MutableDataSet options = new MutableDataSet();
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }

    String getHtml(String markdown) {
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
