package ui.views.fe9;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import ui.model.SkillWeightOptions;
import ui.views.WeightView;
import ui.views.WeightView.WeightViewListener;
import ui.model.WeightedOptions;
import ui.model.WeightedOptions.Weight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillWeightView extends Composite {


	public interface SkillWeightsListener {
		public void onAllItemsDisabled();
		public void onEnableCountChanged(int enabledCount);
	}
	
	private Map<String, WeightView> weightViewsByString = new HashMap<>();
	
	private Label titleLabel;
	
	private SkillWeightsListener listener;
	private boolean allItemsDisabled;
	
	public class WeightListener implements WeightViewListener {
		private void notifyCount() {
			int count = 0;
			for (WeightView view : weightViewsByString.values()) {
				if (view.optionEnabled()) { count++; }
			}
			
			notifyEnableCountChanged(count);
			if (count == 0) {
				notifyAllItemsDisabled();
			}
		}
		
		@Override
		public void onWeightChanged(Weight oldWeight, Weight newWeight) {}

		@Override
		public void onItemEnabled() { notifyCount(); }

		@Override
		public void onItemDisabled() { notifyCount(); }
	}
	
	private WeightListener weightListener = new WeightListener();
	
	private void notifyAllItemsDisabled() {
		allItemsDisabled = true;
		if (listener != null) {
			listener.onAllItemsDisabled();
		}
	}
	
	private void notifyEnableCountChanged(int count) {
		if (listener != null) {
			listener.onEnableCountChanged(count);
		}
	}
	
	public void setListener(SkillWeightsListener listener) {
		this.listener = listener;
	}



	public SkillWeightView(Composite parent, int style, List<String> skillTitles, int numberColumns) {
		super(parent, style);

		FormLayout formLayout = new FormLayout();
		formLayout.marginTop = 10;
		formLayout.marginLeft = 10;
		setLayout(formLayout);
		
		titleLabel = new Label(this, SWT.NONE);
		titleLabel.setText("Skill Weights");

		FormData titleData = new FormData();
		titleData.left = new FormAttachment(0, 0);
		titleLabel.setLayoutData(titleData);
		
		skillTitles.sort(String::compareTo);
		
		Group skillListContainer = new Group(this, SWT.NONE);
		FormData skillListData = new FormData();
		skillListData.top = new FormAttachment(titleLabel, 5);
		skillListContainer.setLayoutData(skillListData);

		GridLayout skillListLayout = new GridLayout(numberColumns, false);
	 	skillListContainer.setLayout(skillListLayout);

		for (String title : skillTitles) {
			WeightView view = new WeightView(title, Weight.NORMAL, skillListContainer);
			view.setListener(weightListener);
			weightViewsByString.put(title, view);
		}
	}
	
	public void setEnabled(boolean enabled) {
		for (WeightView view : weightViewsByString.values()) { view.setEnabled(enabled); }
		
		if (allItemsDisabled && enabled) {
			for (WeightView view : weightViewsByString.values()) { view.setSelected(true); }
			allItemsDisabled = false;
		}
	}
	
	public SkillWeightOptions getSkillWeights() {
		Map<String, WeightedOptions> weights = new HashMap<String, WeightedOptions>();
		for (String skill : weightViewsByString.keySet()) {
			WeightView view = weightViewsByString.get(skill);
			weights.put(skill, view.getOptions());
		}
		
		return new SkillWeightOptions(weights);
	}
	
	public void setSkillWeights(SkillWeightOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			for (String skill : weightViewsByString.keySet()) {
				WeightView view = weightViewsByString.get(skill);
				view.initialize(options.getWeightedOptionsByName(skill));
			}
		}
	}
}
