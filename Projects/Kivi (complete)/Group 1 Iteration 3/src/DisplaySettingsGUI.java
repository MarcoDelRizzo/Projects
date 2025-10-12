import java.awt.*;
import java.io.Serializable;
import javax.swing.*;

public class DisplaySettingsGUI extends JFrame implements Serializable {
    
    private static final long serialVersionUID = 1L;
	private transient KiviBoard board;
	public DisplaySettingsGUI(KiviBoard board) 
	
	{
	
		this.board = board;
       
		
		setTitle("Display Settings");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); 
        setSize(300, 250);
        setLayout(new GridLayout(4, 1));

        
        final int defaultResolutionIndex = 0;

       //resolution
        String[] resolutions = {"1020x860","1020x900","1080x860", "1160x900","1620x1050", "1920x1080"};
        JComboBox<String> resolutionDropdown = new JComboBox<>(resolutions);
        resolutionDropdown.setSelectedIndex(defaultResolutionIndex);
        JPanel resolutionPanel = new JPanel();
        resolutionPanel.add(new JLabel("Resolution:"));
        resolutionPanel.add(resolutionDropdown);
        add(resolutionPanel);

        //colorblind mode
        JPanel colorblindPanel = new JPanel();
        colorblindPanel.add(new JLabel("Colorblind Mode:"));
        JCheckBox colorBlindOn = new JCheckBox("On");
        JCheckBox colorBlindOff = new JCheckBox("Off", true);

        colorBlindOn.addActionListener(_ -> {
            if (colorBlindOn.isSelected()) {
                colorBlindOff.setSelected(false);
            }
        });

        colorBlindOff.addActionListener(_ -> {
            if (colorBlindOff.isSelected()) {
                colorBlindOn.setSelected(false);
            }
        });

        colorblindPanel.add(colorBlindOn);
        colorblindPanel.add(colorBlindOff);
        add(colorblindPanel);

        //buttons panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save Changes");
        JButton discardButton = new JButton("Discard Changes");

        discardButton.addActionListener(e -> {
            resolutionDropdown.setSelectedIndex(defaultResolutionIndex);
            colorBlindOff.setSelected(true);
            colorBlindOn.setSelected(false);
        });
        saveButton.addActionListener(e -> {
            String selectedResolution = (String) resolutionDropdown.getSelectedItem();
            applyResolution(selectedResolution);
            dispose();
        });
        

        buttonPanel.add(saveButton);
        buttonPanel.add(discardButton);
        add(buttonPanel);

        // display Frame
        setLocationRelativeTo(null);
        setVisible(true);
	}
        private void applyResolution(String resolution) {
            String[] dimensions = resolution.split("x");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            if (board != null) {
            	board.updateResolution(width, height);
            }
    }
}