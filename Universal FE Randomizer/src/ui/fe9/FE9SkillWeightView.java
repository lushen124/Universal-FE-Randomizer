package ui.fe9;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import ui.WeightView;
import ui.WeightView.WeightViewListener;
import ui.model.WeightedOptions;
import ui.model.WeightedOptions.Weight;

public class FE9SkillWeightView extends Composite {
	
	public interface FE9SkillWeightsListener {
		public void onAllItemsDisabled();
		public void onEnableCountChanged(int enabledCount);
	}
	
	private Map<String, WeightView> weightViewsByString;
	
	private Label titleLabel;
	
	private FE9SkillWeightsListener listener;
	private boolean allItemsDisabled;
	
	private class FE9WeightListener implements WeightViewListener {
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
	
	private FE9WeightListener weightListener = new FE9WeightListener();
	
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
	
	public void setListener(FE9SkillWeightsListener listener) {
		this.listener = listener;
	}

	public FE9SkillWeightView(Composite parent, int style, List<String> skillTitles) {
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
		
		Composite header = new Composite(this, SWT.NONE);
		
		FormData headerData = new FormData();
		headerData.left = new FormAttachment(0, 5);
		headerData.top = new FormAttachment(titleLabel, 5);
		headerData.right = new FormAttachment(100, -5);
		header.setLayoutData(headerData);
		
		FormLayout headerLayout = new FormLayout();
		header.setLayout(headerLayout);
		
		Label allowLabel = new Label(header, SWT.NONE);
		allowLabel.setText("Allow?");
		
		FormData allowData = new FormData();
		allowData.left = new FormAttachment(0, 0);
		allowData.top = new FormAttachment(0, 0);
		allowLabel.setLayoutData(allowData);
		
		Label lessLikely = new Label(header, SWT.NONE);
		lessLikely.setText("Less Likely");
		
		FormData lessData = new FormData();
		lessData.left = new FormAttachment(0, 80);
		lessData.top = new FormAttachment(allowLabel, 0, SWT.CENTER);
		lessLikely.setLayoutData(lessData);
		
		Label moreLikely = new Label(header, SWT.NONE);
		moreLikely.setText("More Likely");
		
		FormData moreData = new FormData();
		moreData.right = new FormAttachment(100, -5);
		moreData.top = new FormAttachment(allowLabel, 0, SWT.CENTER);
		moreLikely.setLayoutData(moreData);
		
		skillTitles.sort(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		
		Composite previousView = header;
		weightViewsByString = new HashMap<String, WeightView>();
		
		for (String title : skillTitles) {
			WeightView view = new WeightView(title, Weight.NORMAL, this, SWT.NONE);
			view.setListener(weightListener);
			
			FormData viewData = new FormData();
			viewData.left = new FormAttachment(previousView, 0, SWT.LEFT);
			viewData.top = new FormAttachment(previousView, 5);
			viewData.right = new FormAttachment(100, -5);
			view.setLayoutData(viewData);
			
			previousView = view;
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
	
	public FE9SkillWeightOptions getSkillWeights() {
		Map<String, WeightedOptions> weights = new HashMap<String, WeightedOptions>();
		for (String skill : weightViewsByString.keySet()) {
			WeightView view = weightViewsByString.get(skill);
			weights.put(skill, view.getWeightedOptions());
		}
		
		return new FE9SkillWeightOptions(weights);
	}
	
	public void setSkillWeights(FE9SkillWeightOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			for (String skill : weightViewsByString.keySet()) {
				WeightView view = weightViewsByString.get(skill);
				view.setWeightedOptions(options.getWeightedOptionsByName(skill));
			}
		}
	}
}
