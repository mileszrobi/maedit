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
        
        options.set(HtmlRenderer.SOURCE_POSITION_ATTRIBUTE, "sourcePosition");
        
        parser = Parser.builder(options).build();
        renderer = HtmlRenderer.builder(options).build();
    }
    
    public Node getHtml(String markdown) {
        return parser.parse(markdown);
    }

    public String getHtmlAsString(String markdown) {
        String cucc = renderer.render(getHtml(markdown));
        System.out.println(cucc);
        return cucc;
    }
}
