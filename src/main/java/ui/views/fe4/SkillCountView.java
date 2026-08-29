package ui.views.fe4;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import ui.model.fe4.SkillCountDistributionOptions;
import ui.views.WeightView;
import ui.views.WeightView.WeightViewListener;
import ui.model.WeightedOptions.Weight;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class SkillCountView extends Composite {
	
	private WeightView zeroView;
	private WeightView oneView;
	private WeightView twoView;
	private WeightView threeView;
	
	private boolean isEnabled;
	
	private Set<WeightView> allViews;
	
	public interface SkillCountListener {
		public void onAllItemsDisabled();
	}
	
	private SkillCountListener listener;
	private boolean allItemsDisabled;
	
	private void notifyAllItemsDisabled() {
		allItemsDisabled = true;
		if (listener != null) {
			listener.onAllItemsDisabled();
		}
	}
	
	public void setListener(SkillCountListener listener) {
		this.listener = listener;
	}
	
	public SkillCountView(Composite parent, int style) {
		super(parent, style);
		
		FormLayout formLayout = new FormLayout();
		formLayout.marginTop = 5;
		formLayout.marginLeft = 10;
		setLayout(formLayout);
		
		Label titleLabel = new Label(this, SWT.NONE);
		titleLabel.setText("Number of Skills per Character");
		
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
		lessLikely.setText("Chance");
		
		FormData lessData = new FormData();
		lessData.right = new FormAttachment(100, -5);
		lessData.top = new FormAttachment(allowLabel, 0, SWT.CENTER);
		lessLikely.setLayoutData(lessData);

		zeroView = new WeightView("No Skills", Weight.VERY_LOW, this);
		zeroView.setListener(new WeightViewListener() {
			@Override
			public void onWeightChanged(Weight oldWeight, Weight newWeight) {}
			
			@Override
			public void onItemEnabled() {}
			
			@Override
			public void onItemDisabled() {
				for (WeightView view : allViews) { if (view.optionEnabled()) { return; } }
				notifyAllItemsDisabled();
			}
		});
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(header, 5);
		viewData.right = new FormAttachment(100, -5);
		zeroView.setLayoutData(viewData);
		
		oneView = new WeightView("1 Skill", Weight.HIGH, this);
		oneView.setListener(new WeightViewListener() {
			@Override
			public void onWeightChanged(Weight oldWeight, Weight newWeight) {}
			
			@Override
			public void onItemEnabled() {}
			
			@Override
			public void onItemDisabled() {
				for (WeightView view : allViews) { if (view.optionEnabled()) { return; } }
				notifyAllItemsDisabled();
			}
		});
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(zeroView, 0);
		viewData.right = new FormAttachment(100, -5);
		oneView.setLayoutData(viewData);
		
		twoView = new WeightView("2 Skills", Weight.NORMAL, this);
		twoView.setListener(new WeightViewListener() {
			@Override
			public void onWeightChanged(Weight oldWeight, Weight newWeight) {}
			
			@Override
			public void onItemEnabled() {}
			
			@Override
			public void onItemDisabled() {
				for (WeightView view : allViews) { if (view.optionEnabled()) { return; } }
				notifyAllItemsDisabled();
			}
		});
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(oneView, 0);
		viewData.right = new FormAttachment(100, -5);
		twoView.setLayoutData(viewData);
		
		threeView = new WeightView("3 Skills", Weight.VERY_LOW, this);
		threeView.setListener(new WeightViewListener() {
			@Override
			public void onWeightChanged(Weight oldWeight, Weight newWeight) {}
			
			@Override
			public void onItemEnabled() {}
			
			@Override
			public void onItemDisabled() {
				for (WeightView view : allViews) { if (view.optionEnabled()) { return; } }
				notifyAllItemsDisabled();
			}
		});
		
		viewData = new FormData();
		viewData.left = new FormAttachment(header, 0, SWT.LEFT);
		viewData.top = new FormAttachment(twoView, 0);
		viewData.right = new FormAttachment(100, -5);
		threeView.setLayoutData(viewData);
		
		allViews = new HashSet<WeightView>(Arrays.asList(zeroView, oneView, twoView, threeView));
	}
	
	public void setMaxSkillCount(int max) {
		if (!isEnabled) { return; }
		threeView.setEnabled(max >= 3);
		twoView.setEnabled(max >= 2);
		oneView.setEnabled(max >= 1);
		zeroView.setEnabled(true);
	}
	
	public void setEnabled(boolean enabled) {
		isEnabled = enabled;
		
		for (WeightView view : allViews) { view.setEnabled(enabled); }
		
		if (allItemsDisabled && enabled) {
			for (WeightView view : allViews) { view.setSelected(true); }
			allItemsDisabled = false;
		}
	}
	
	public SkillCountDistributionOptions getSkillCountDistribution() {
		return new SkillCountDistributionOptions(zeroView.getOptions(), oneView.getOptions(), twoView.getOptions(), threeView.getOptions());
	}

	public void setSkillCountDistribution(SkillCountDistributionOptions options) {
		if (options == null) {
			// Shouldn't happen.
		} else {
			zeroView.initialize(options.zeroSkillsChance);
			oneView.initialize(options.oneSkillChance);
			twoView.initialize(options.twoSkillChance);
			threeView.initialize(options.threeSkillChance);
		}
	}
}
