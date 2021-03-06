package com.github.blutorange.primefaces.component.monacoeditor;

import com.github.blutorange.primefaces.util.Constants;
import org.primefaces.context.RequestContext;
import org.primefaces.renderkit.InputRenderer;
import org.primefaces.util.ComponentUtils;
import org.primefaces.util.HTML;
import org.primefaces.util.WidgetBuilder;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.convert.Converter;
import javax.faces.render.FacesRenderer;
import java.io.IOException;
import java.util.Map;

@FacesRenderer(componentFamily = MonacoEditor.COMPONENT_FAMILY, rendererType = MonacoEditor.RENDERER_TYPE)
public class MonacoEditorRenderer extends InputRenderer {
    @Override
    public void decode(final FacesContext context, final UIComponent component) {
        final MonacoEditor monacoEditor = (MonacoEditor) component;

        // Do not allow modifications if component is not editable.
        if (monacoEditor.isDisabled() || monacoEditor.isReadonly()) {
            return;
        }

        // Decode value
        final String clientId = monacoEditor.getClientId() + "_input";
        final Map<String, String> params = context.getExternalContext().getRequestParameterMap();
        if (params.containsKey(clientId)) {
            monacoEditor.setSubmittedValue(params.get(clientId));
        }

        // Decode behaviors
        decodeBehaviors(context, component);
    }

    @Override
    public Object getConvertedValue(final FacesContext context, final UIComponent component, final Object submittedValue) {
        final MonacoEditor monacoEditor = (MonacoEditor) component;
        final String value = (String) submittedValue;
        final Converter converter = ComponentUtils.getConverter(context, monacoEditor);

        if (converter != null) {
            return converter.getAsObject(context, monacoEditor, value);
        }

        return value;
    }

    @Override
    public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
        final MonacoEditor monacoEditor = (MonacoEditor) component;
        encodeMarkup(context, monacoEditor);
        encodeScript(context, monacoEditor);
    }

    protected void encodeMarkup(final FacesContext context, final MonacoEditor monacoEditor) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = monacoEditor.getClientId();

        String style = monacoEditor.getStyle() != null ? monacoEditor.getStyle() : "";
        style = style.concat(";");
        if (monacoEditor.getWidth() != null && !monacoEditor.getWidth().isEmpty()) {
            style = style.concat("width:" + monacoEditor.getWidth() + ";");
        }
        if (monacoEditor.getHeight() != null && !monacoEditor.getHeight().isEmpty()) {
            style = style.concat("height:" + monacoEditor.getHeight() + ";");
        }
        final StringBuilder styleClass = new StringBuilder();
        styleClass.append("ui-monaco-editor ");
        if (monacoEditor.isDisabled() || monacoEditor.isReadonly()) {
            styleClass.append("ui-state-disabled ");
        }
        if (monacoEditor.getStyleClass() != null) {
            styleClass.append(monacoEditor.getStyleClass());
        }

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId, null);
        writer.writeAttribute("data-widget-var", monacoEditor.resolveWidgetVar(), null);
        writer.writeAttribute("class", styleClass.toString(), null);
        writer.writeAttribute("style", style, null);

        encodeHiddenInput(context, monacoEditor);
        encodeMonacoEditor(context, monacoEditor);

        writer.endElement("div");
    }

    protected void encodeMonacoEditor(final FacesContext context, final MonacoEditor monacoEditor) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = monacoEditor.getClientId();

        writer.startElement("div", null);
        writer.writeAttribute("id", clientId + "_editor", null);
        writer.writeAttribute("class", "ui-monaco-editor-ed", null);
        writer.writeAttribute("style", "width:100%;height:100%;", null);
        writer.endElement("div");
    }

    protected void encodeHiddenInput(final FacesContext context, final MonacoEditor monacoEditor) throws IOException {
        final ResponseWriter writer = context.getResponseWriter();
        final String clientId = monacoEditor.getClientId();

        writer.startElement("div", null);
        writer.writeAttribute("class", "ui-helper-hidden-accessible", null);

        writer.startElement("textarea", monacoEditor);
        writer.writeAttribute("id", clientId + "_input", null);
        writer.writeAttribute("name", clientId + "_input", null);
        writer.writeAttribute("autocomplete", "off", null);
        if (monacoEditor.isReadonly()) {
            writer.writeAttribute("readonly", "readonly", null);
        }
        if (monacoEditor.isDisabled()) {
            writer.writeAttribute("disabled", "disabled", null);
        }
        if (monacoEditor.getTabindex() != null) {
            writer.writeAttribute("tabindex", monacoEditor.getTabindex(), null);
        }

        renderPassThruAttributes(context, monacoEditor, HTML.TEXTAREA_ATTRS_WITHOUT_EVENTS);

        final String valueToRender = ComponentUtils.getValueToRender(context, monacoEditor);
        if (valueToRender != null) {
            writer.writeText(valueToRender, null);
        }
        writer.endElement("textarea");

        writer.endElement("div");
    }

    protected void encodeScript(final FacesContext context, final MonacoEditor monacoEditor) throws IOException {
        final WidgetBuilder wb = RequestContext.getCurrentInstance().getWidgetBuilder();

        wb.initWithDomReady("ExtMonacoEditor", monacoEditor.resolveWidgetVar(), monacoEditor.getClientId());

        wb.attr("version", Constants.VERSION);

        wb.attr(MonacoEditor.PropertyKeys.AUTO_RESIZE.toString(), monacoEditor.isAutoResize(), MonacoEditor.DEFAULT_AUTO_RESIZE);
        wb.attr(MonacoEditor.PropertyKeys.DISABLED.toString(), monacoEditor.isDisabled(), MonacoEditor.DEFAULT_DISABLED);
        wb.attr(MonacoEditor.PropertyKeys.EXTENDER.toString(), monacoEditor.getExtender(), MonacoEditor.DEFAULT_EXTENDER);
        wb.attr(MonacoEditor.PropertyKeys.EDITOR_OPTIONS.toString(), monacoEditor.getEditorOptions().toString());
        wb.attr(MonacoEditor.PropertyKeys.READONLY.toString(), monacoEditor.isReadonly(), MonacoEditor.DEFAULT_READONLY);
        wb.attr(MonacoEditor.PropertyKeys.UI_LANGUAGE.toString(), monacoEditor.getUiLanguage(), MonacoEditor.DEFAULT_UI_LANGUAGE);
        wb.attr(MonacoEditor.PropertyKeys.UI_LANGUAGE_URI.toString(), monacoEditor.getUiLanguageUri(), MonacoEditor.DEFAULT_UI_LANGUAGE_URI);

        wb.attr(MonacoEditor.PropertyKeys.ONBLUR.toString(), monacoEditor.getOnblur(), MonacoEditor.DEFAULT_ONBLUR);
        wb.attr(MonacoEditor.PropertyKeys.ONFOCUS.toString(), monacoEditor.getOnfocus(), MonacoEditor.DEFAULT_ONFOCUS);
        wb.attr(MonacoEditor.PropertyKeys.ONCHANGE.toString(), monacoEditor.getOnchange(), MonacoEditor.DEFAULT_ONCHANGE);
        wb.attr(MonacoEditor.PropertyKeys.ONPASTE.toString(), monacoEditor.getOnpaste(), MonacoEditor.DEFAULT_ONPASTE);
        wb.attr(MonacoEditor.PropertyKeys.ONMOUSEDOWN.toString(), monacoEditor.getOnmousedown(), MonacoEditor.DEFAULT_ONMOUSEDOWN);
        wb.attr(MonacoEditor.PropertyKeys.ONMOUSEMOVE.toString(), monacoEditor.getOnmousemove(), MonacoEditor.DEFAULT_ONMOUSEMOVE);
        wb.attr(MonacoEditor.PropertyKeys.ONMOUSEUP.toString(), monacoEditor.getOnmouseup(), MonacoEditor.DEFAULT_ONMOUSEUP);
        wb.attr(MonacoEditor.PropertyKeys.ONKEYDOWN.toString(), monacoEditor.getOnkeydown(), MonacoEditor.DEFAULT_ONKEYDOWN);
        wb.attr(MonacoEditor.PropertyKeys.ONKEYPRESS.toString(), monacoEditor.getOnkeypress(), MonacoEditor.DEFAULT_ONKEYPRESS);
        wb.attr(MonacoEditor.PropertyKeys.ONKEYUP.toString(), monacoEditor.getOnkeyup(), MonacoEditor.DEFAULT_ONKEYUP);

        encodeClientBehaviors(context, monacoEditor);
        wb.finish();
    }
}