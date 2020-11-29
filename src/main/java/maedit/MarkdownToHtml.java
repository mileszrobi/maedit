package maedit;

import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;

public class MarkdownToHtml {
    Parser parser;
    HtmlRenderer renderer;
    
    public MarkdownToHtml() {
        MutableDataSet options = new MutableDataSet();
        
        options.set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, "sourcePosition");
        
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }
    
    public Node getHtml(String markdown) {
        return parser.parse(markdown);
    }

    public String getHtmlAsString(String markdown) {
        return renderer.render(getHtml(markdown));
    }
}
