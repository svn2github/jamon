package org.jamon.eclipse.editor.preferences;

import java.util.EnumMap;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class SyntaxPage extends PreferencePage implements IWorkbenchPreferencePage
{
    private List styleList = null;
    private ColorSelector m_foregroundColorSelector = null;
    private ColorSelector m_backgroundColorSelector = null;
    private EnumMap<StyleOption, Button> styleOptionButtons =
        new EnumMap<StyleOption, Button>(StyleOption.class);

    private SyntaxPreferences m_syntaxPreferences;
    private SyntaxType m_currentSelectedSyntaxType = null;

    private final class StyleListSelectionListener implements SelectionListener
    {

        public void widgetDefaultSelected(SelectionEvent e) {}

        public void widgetSelected(SelectionEvent e)
        {
            saveCurrentStyle();
            int selectionIndex = styleList.getSelectionIndex();
            if (selectionIndex >= 0 && selectionIndex < SyntaxType.values().length)
            {
                setEnablement(true);
                m_currentSelectedSyntaxType = SyntaxType.values()[selectionIndex];
                Style style = m_syntaxPreferences.getStyle(m_currentSelectedSyntaxType);
                m_foregroundColorSelector.setColorValue(style.getForeground());
                m_backgroundColorSelector.setColorValue(style.getBackground());
                for (StyleOption styleOption: StyleOption.values())
                {
                    styleOptionButtons.get(styleOption).setSelection(
                        style.isOptionSet(styleOption));
                }
            }
            else
            {
                m_currentSelectedSyntaxType = null;
                setEnablement(false);
            }
        }
    }

    public SyntaxPage()
    {
        initialize();
    }

    public SyntaxPage(String title)
    {
        super(title);
        initialize();
    }

    public SyntaxPage(String title, ImageDescriptor image)
    {
        super(title, image);
        initialize();
    }


    private void initialize()
    {
        m_syntaxPreferences = new SyntaxPreferences();
        setPreferenceStore(SyntaxPreferences.getPreferenceStore());
    }



    /**
     * This method initializes group
     *
     */
    private void createOptionsGroup(Composite parent)
    {
        Group optionsGroup = new Group(parent, SWT.NONE);
        optionsGroup.setLayout(new GridLayout(2, true));

        new Label(optionsGroup, SWT.NONE).setText("Foreground:");
        m_foregroundColorSelector = new ColorSelector(optionsGroup);

        new Label(optionsGroup, SWT.NONE).setText("Background:");
        m_backgroundColorSelector = new ColorSelector(optionsGroup);


        for (StyleOption styleOption: StyleOption.values())
        {
            Button checkbox = new Button(optionsGroup, SWT.CHECK);
            GridData gridData = new GridData();
            gridData.horizontalSpan = 2;
            checkbox.setLayoutData(gridData);
            checkbox.setText(styleOption.getLabel());
            styleOptionButtons.put(styleOption, checkbox);
        }
        setEnablement(false);
    }

    @Override protected Control createContents(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NONE);
        GridData gridData = new GridData();
        gridData.verticalAlignment = GridData.FILL;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(2, true));
        Label elementLabel = new Label(composite, SWT.NONE);
        elementLabel.setText("Element");

        GridData labelGridData = new GridData();
        labelGridData.horizontalSpan = 2;
        elementLabel.setLayoutData(labelGridData);

        styleList = new List(composite, SWT.NONE);
        GridData styleListGridData = new GridData();
        styleListGridData.horizontalAlignment = GridData.FILL;
        styleListGridData.grabExcessHorizontalSpace = false;
        styleListGridData.verticalAlignment = GridData.BEGINNING;
        styleList.setLayoutData(styleListGridData);
        styleList.setItems(SyntaxType.getNames());

        styleList.addSelectionListener(new StyleListSelectionListener());

        createOptionsGroup(composite);
        return composite;
    }

    public void init(IWorkbench workbench)
    {
    }

    @Override public boolean performOk()
    {
        saveCurrentStyle();
        m_syntaxPreferences.apply();
        return true;
    }

    @Override protected void performDefaults()
    {
        m_syntaxPreferences.restoreDefaults();
        super.performDefaults();
    }

    private void saveCurrentStyle()
    {
        if (m_currentSelectedSyntaxType != null) {
            Style style = m_syntaxPreferences.getStyle(m_currentSelectedSyntaxType);
            style.setForeground(m_foregroundColorSelector.getColorValue());
            style.setBackground(m_backgroundColorSelector.getColorValue());
            for (StyleOption styleOption: StyleOption.values())
            {
                style.setOption(
                    styleOption, styleOptionButtons.get(styleOption).getSelection());
            }
            m_syntaxPreferences.setStyle(m_currentSelectedSyntaxType, style);
        }
    }

    private void setEnablement(boolean enabled)
    {
        m_foregroundColorSelector.setEnabled(enabled);
        m_backgroundColorSelector.setEnabled(enabled);
        for (Button button: styleOptionButtons.values())
        {
            button.setEnabled(enabled);
        }
    }
}