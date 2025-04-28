package ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import fedata.gcnwii.fe9.FE9Data;
import fedata.gcnwii.fe9.FE9Item;
import io.gcn.GCNISOException;
import io.gcn.GCNISOHandler;
import random.gcnwii.fe9.loader.FE9CommonTextLoader;
import random.gcnwii.fe9.loader.FE9ItemDataLoader;
import ui.component.LabelFieldView;
import util.WhyDoesJavaNotHaveThese;

public class ItemDataView extends Composite {

	private GCNISOHandler handler;
	private FE9ItemDataLoader itemData;
	private FE9CommonTextLoader textData;
	
	private Combo itemDropdown;
	
	private Group infoGroup;
	private LabelFieldView iid;
	private LabelFieldView miid;
	private LabelFieldView mhi;
	private Label descriptionLabel;
	private LabelFieldView equipType;
	private LabelFieldView realType;
	private LabelFieldView rank;
	
	private Group traitsGroup;
	private LabelFieldView trait1;
	private LabelFieldView trait2;
	private LabelFieldView trait3;
	private LabelFieldView trait4;
	private LabelFieldView trait5;
	private LabelFieldView trait6;
	private LabelFieldView effectiveness1;
	private LabelFieldView effectiveness2;
	private LabelFieldView animation1;
	private LabelFieldView animation2;
	
	private Group statGroup;
	private LabelFieldView costPerUse;
	private LabelFieldView durability;
	private LabelFieldView might;
	private LabelFieldView accuracy;
	private LabelFieldView weight;
	private LabelFieldView critical;
	private LabelFieldView range;
	private LabelFieldView weaponXP;
	private LabelFieldView statBoosts;
	private LabelFieldView growthBoosts;
	private LabelFieldView growthPenalties;
	
	private Group miscGroup;
	private LabelFieldView iconNumber;
	private LabelFieldView x5Dx5F;
	
	public ItemDataView(Composite parent, int style, GCNISOHandler isoHandler) throws GCNISOException {
		super(parent, style);
		handler = isoHandler;
		textData = new FE9CommonTextLoader(handler);
		itemData = new FE9ItemDataLoader(handler, textData);
		
		FormLayout mainLayout = new FormLayout();
		mainLayout.marginWidth = 10;
		mainLayout.marginHeight = 10;
		setLayout(mainLayout);
		
		itemDropdown = new Combo(this, SWT.READ_ONLY | SWT.DROP_DOWN);
		itemDropdown.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		itemDropdown.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				setItem(itemData.allItems().get(itemDropdown.getSelectionIndex()));
			}
		});
		
		FormData dropdownData = new FormData();
		dropdownData.left = new FormAttachment(0, 0);
		dropdownData.top = new FormAttachment(0, 0);
		dropdownData.right = new FormAttachment(100, 0);
		itemDropdown.setLayoutData(dropdownData);
		
		infoGroup = new Group(this, SWT.NONE);
		infoGroup.setText("Information");
		
		FormLayout infoLayout = new FormLayout();
		infoLayout.marginWidth = 5;
		infoLayout.marginHeight = 5;
		infoGroup.setLayout(infoLayout);
		
		FormData infoData = new FormData();
		infoData.left = new FormAttachment(0, 0);
		infoData.top = new FormAttachment(itemDropdown, 10);
		infoData.width = 320;
		infoGroup.setLayoutData(infoData);
		
		iid = new LabelFieldView(infoGroup, SWT.NONE);
		iid.setLabel("IID: ");
		iid.setField("(null)");
		
		FormData viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		iid.setLayoutData(viewData);
		
		miid = new LabelFieldView(infoGroup, SWT.NONE);
		miid.setLabel("MIID: ");
		miid.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(iid, 10);
		viewData.right = new FormAttachment(100, 0);
		miid.setLayoutData(viewData);
		
		mhi = new LabelFieldView(infoGroup, SWT.NONE);
		mhi.setLabel("MH_I: ");
		mhi.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(miid, 10);
		viewData.right = new FormAttachment(100, 0);
		mhi.setLayoutData(viewData);
		
		descriptionLabel = new Label(infoGroup, SWT.WRAP);
		descriptionLabel.setText("");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(mhi, 10);
		viewData.right = new FormAttachment(100, 0);
		descriptionLabel.setLayoutData(viewData);
		
		equipType = new LabelFieldView(infoGroup, SWT.NONE);
		equipType.setLabel("Equip Type: ");
		equipType.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(descriptionLabel, 10);
		viewData.right = new FormAttachment(100, 0);
		equipType.setLayoutData(viewData);
		
		realType = new LabelFieldView(infoGroup, SWT.NONE);
		realType.setLabel("Real Type: ");
		realType.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(equipType, 10);
		viewData.right = new FormAttachment(100, 0);
		realType.setLayoutData(viewData);
		
		rank = new LabelFieldView(infoGroup, SWT.NONE);
		rank.setLabel("Rank: ");
		rank.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(realType, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		rank.setLayoutData(viewData);
		
		traitsGroup = new Group(this, SWT.NONE);
		traitsGroup.setText("Traits");
		
		FormLayout traitLayout = new FormLayout();
		traitLayout.marginWidth = 5;
		traitLayout.marginHeight = 5;
		traitsGroup.setLayout(traitLayout);
		
		FormData traitData = new FormData();
		traitData.left = new FormAttachment(infoGroup, 10);
		traitData.top = new FormAttachment(itemDropdown, 10);
		traitData.width = 320;
		traitsGroup.setLayoutData(traitData);
		
		trait1 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait1.setLabel("Trait 1: ");
		trait1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		trait1.setLayoutData(viewData);
		
		trait2 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait2.setLabel("Trait 2: ");
		trait2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait1, 10);
		viewData.right = new FormAttachment(100, 0);
		trait2.setLayoutData(viewData);
		
		trait3 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait3.setLabel("Trait 3: ");
		trait3.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait2, 10);
		viewData.right = new FormAttachment(100, 0);
		trait3.setLayoutData(viewData);
		
		trait4 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait4.setLabel("Trait 4: ");
		trait4.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait3, 10);
		viewData.right = new FormAttachment(100, 0);
		trait4.setLayoutData(viewData);
		
		trait5 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait5.setLabel("Trait 5: ");
		trait5.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait4, 10);
		viewData.right = new FormAttachment(100, 0);
		trait5.setLayoutData(viewData);
		
		trait6 = new LabelFieldView(traitsGroup, SWT.NONE);
		trait6.setLabel("Trait 6: ");
		trait6.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait5, 10);
		viewData.right = new FormAttachment(100, 0);
		trait6.setLayoutData(viewData);
		
		effectiveness1 = new LabelFieldView(traitsGroup, SWT.NONE);
		effectiveness1.setLabel("Effectiveness 1: ");
		effectiveness1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(trait6, 10);
		viewData.right = new FormAttachment(100, 0);
		effectiveness1.setLayoutData(viewData);
		
		effectiveness2 = new LabelFieldView(traitsGroup, SWT.NONE);
		effectiveness2.setLabel("Effectiveness 2: ");
		effectiveness2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(effectiveness1, 10);
		viewData.right = new FormAttachment(100, 0);
		effectiveness2.setLayoutData(viewData);
		
		animation1 = new LabelFieldView(traitsGroup, SWT.NONE);
		animation1.setLabel("Animation 1: ");
		animation1.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(effectiveness2, 10);
		viewData.right = new FormAttachment(100, 0);
		animation1.setLayoutData(viewData);
		
		animation2 = new LabelFieldView(traitsGroup, SWT.NONE);
		animation2.setLabel("Animation 2: ");
		animation2.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(animation1, 10);
		viewData.bottom = new FormAttachment(100, 0);
		viewData.right = new FormAttachment(100, 0);
		animation2.setLayoutData(viewData);
		
		statGroup = new Group(this, SWT.NONE);
		statGroup.setText("Stats");
		
		FormLayout statLayout = new FormLayout();
		statLayout.marginWidth = 5;
		statLayout.marginHeight = 5;
		statGroup.setLayout(statLayout);
		
		FormData statData = new FormData();
		statData.left = new FormAttachment(traitsGroup, 10);
		statData.top = new FormAttachment(itemDropdown, 10);
		statData.width = 320;
		statGroup.setLayoutData(statData);
		
		costPerUse = new LabelFieldView(statGroup, SWT.NONE);
		costPerUse.setLabel("Cost per Use: ");
		costPerUse.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		costPerUse.setLayoutData(viewData);
		
		durability = new LabelFieldView(statGroup, SWT.NONE);
		durability.setLabel("Durability: ");
		durability.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(costPerUse, 10);
		viewData.right = new FormAttachment(100, 0);
		durability.setLayoutData(viewData);
		
		might = new LabelFieldView(statGroup, SWT.NONE);
		might.setLabel("Might: ");
		might.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(durability, 10);
		viewData.right = new FormAttachment(100, 0);
		might.setLayoutData(viewData);
		
		accuracy = new LabelFieldView(statGroup, SWT.NONE);
		accuracy.setLabel("Hit: ");
		accuracy.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(might, 10);
		viewData.right = new FormAttachment(100, 0);
		accuracy.setLayoutData(viewData);
		
		weight = new LabelFieldView(statGroup, SWT.NONE);
		weight.setLabel("Weight: ");
		weight.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(accuracy, 10);
		viewData.right = new FormAttachment(100, 0);
		weight.setLayoutData(viewData);
		
		critical = new LabelFieldView(statGroup, SWT.NONE);
		critical.setLabel("Critical: ");
		critical.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weight, 10);
		viewData.right = new FormAttachment(100, 0);
		critical.setLayoutData(viewData);
		
		range = new LabelFieldView(statGroup, SWT.NONE);
		range.setLabel("Range: ");
		range.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(critical, 10);
		viewData.right = new FormAttachment(100, 0);
		range.setLayoutData(viewData);
		
		weaponXP = new LabelFieldView(statGroup, SWT.NONE);
		weaponXP.setLabel("Weapon EXP: ");
		weaponXP.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(range, 10);
		viewData.right = new FormAttachment(100, 0);
		weaponXP.setLayoutData(viewData);
		
		statBoosts = new LabelFieldView(statGroup, SWT.NONE);
		statBoosts.setLabel("Stat Boost: ");
		statBoosts.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(weaponXP, 10);
		viewData.right = new FormAttachment(100, 0);
		statBoosts.setLayoutData(viewData);
		
		growthBoosts = new LabelFieldView(statGroup, SWT.NONE);
		growthBoosts.setLabel("Growth Boost: ");
		growthBoosts.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(statBoosts, 10);
		viewData.right = new FormAttachment(100, 0);
		growthBoosts.setLayoutData(viewData);
		
		growthPenalties = new LabelFieldView(statGroup, SWT.NONE);
		growthPenalties.setLabel("Growth Penalties: ");
		growthPenalties.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(growthBoosts, 10);
		viewData.right = new FormAttachment(100, 0);
		viewData.bottom = new FormAttachment(100, 0);
		growthPenalties.setLayoutData(viewData);
		
		miscGroup = new Group(this, SWT.NONE);
		miscGroup.setText("Miscellaneous");
		
		FormLayout miscLayout = new FormLayout();
		miscLayout.marginWidth = 5;
		miscLayout.marginHeight = 5;
		miscGroup.setLayout(miscLayout);
		
		FormData miscData = new FormData();
		miscData.left = new FormAttachment(statGroup, 10);
		miscData.top = new FormAttachment(itemDropdown, 10);
		miscData.width = 270;
		miscData.right = new FormAttachment(100, 0);
		miscGroup.setLayoutData(miscData);
		
		iconNumber = new LabelFieldView(miscGroup, SWT.NONE);
		iconNumber.setLabel("Item Icon: ");
		iconNumber.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(0, 0);
		viewData.right = new FormAttachment(100, 0);
		iconNumber.setLayoutData(viewData);
		
		x5Dx5F = new LabelFieldView(miscGroup, SWT.NONE);
		x5Dx5F.setLabel("0x5D ~ 0x5F: ");
		x5Dx5F.setField("(null)");
		
		viewData = new FormData();
		viewData.left = new FormAttachment(0, 0);
		viewData.top = new FormAttachment(iconNumber, 10);
		viewData.bottom = new FormAttachment(100, 0);
		viewData.right = new FormAttachment(100, 0);
		x5Dx5F.setLayoutData(viewData);
		
		for (FE9Item item : itemData.allItems()) {
			itemDropdown.add(itemData.iidOfItem(item) + " (" + itemData.getDisplayName(item) + ")");
		}
	}
	
	private void setItem(FE9Item item) {
		iid.setField(itemData.iidOfItem(item));
		miid.setField(itemData.getMIIDOfItem(item) + " (" + textData.textStringForIdentifier(itemData.getMIIDOfItem(item)) + ")");
		mhi.setField(itemData.getMHIForItem(item));
		String descriptionString = textData.textStringForIdentifier(itemData.getMHIForItem(item));
		descriptionLabel.setText(descriptionString != null ? descriptionString : "No description.");
		equipType.setField(itemData.getEquipType(item));
		realType.setField(itemData.getRealType(item));
		rank.setField(itemData.getRank(item));
		
		String[] traits = itemData.getItemTraits(item);
		trait1.setField(traits[0] != null ? traits[0] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[0]).toString() + ")" : null);
		trait2.setField(traits[1] != null ? traits[1] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[1]).toString() + ")" : null);
		trait3.setField(traits[2] != null ? traits[2] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[2]).toString() + ")" : null);
		trait4.setField(traits[3] != null ? traits[3] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[3]).toString() + ")" : null);
		trait5.setField(traits[4] != null ? traits[4] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[4]).toString() + ")" : null);
		trait6.setField(traits[5] != null ? traits[5] + " (" + FE9Data.Item.WeaponTraits.traitWithString(traits[5]).toString() + ")" : null);
		
		effectiveness1.setField(itemData.getEffectiveness1ForItem(item));
		effectiveness2.setField(itemData.getEffectiveness2ForItem(item));
		animation1.setField(itemData.getAnimation1ForItem(item));
		animation2.setField(itemData.getAnimation2ForItem(item));
		
		costPerUse.setField(Integer.toString(item.getItemCost()));
		durability.setField(Integer.toString(item.getItemDurability()));
		might.setField(Integer.toString(item.getItemMight()));
		accuracy.setField(Integer.toString(item.getItemAccuracy()));
		weight.setField(Integer.toString(item.getItemWeight()));
		critical.setField(Integer.toString(item.getItemCritical()));
		range.setField(item.getMinimumRange() + " ~ " + item.getMaximumRange());
		weaponXP.setField(Integer.toString(item.getWeaponExperience()));
		
		statBoosts.setField(
				(item.getHPBonus() > 0 ? "+" + item.getHPBonus() + " HP " : "") +
				(item.getSTRBonus() > 0 ? "+" + item.getSTRBonus() + " STR " : "") +
				(item.getMAGBonus() > 0 ? "+" + item.getMAGBonus() + " MAG " : "") +
				(item.getSKLBonus() > 0 ? "+" + item.getSKLBonus() + " SKL " : "") +
				(item.getSPDBonus() > 0 ? "+" + item.getSPDBonus() + " SPD " : "") +
				(item.getLCKBonus() > 0 ? "+" + item.getLCKBonus() + " LCK " : "") +
				(item.getDEFBonus() > 0 ? "+" + item.getDEFBonus() + " DEF " : "") +
				(item.getRESBonus() > 0 ? "+" + item.getRESBonus() + " RES " : "")
				);
		
		growthBoosts.setField(
				(item.getHPGrowthBonus() > 0 ? "+" + item.getHPGrowthBonus() + "% HP" : "") +
				(item.getSTRGrowthBonus() > 0 ? "+" + item.getSTRGrowthBonus() + "% STR" : "") +
				(item.getMAGGrowthBonus() > 0 ? "+" + item.getMAGGrowthBonus() + "% MAG" : "") +
				(item.getSKLGrowthBonus() > 0 ? "+" + item.getSKLGrowthBonus() + "% SKL" : "") +
				(item.getSPDGrowthBonus() > 0 ? "+" + item.getSPDGrowthBonus() + "% SPD" : "") +
				(item.getLCKGrowthBonus() > 0 ? "+" + item.getLCKGrowthBonus() + "% LCK" : "") +
				(item.getDEFGrowthBonus() > 0 ? "+" + item.getDEFGrowthBonus() + "% DEF" : "") +
				(item.getRESGrowthBonus() > 0 ? "+" + item.getRESGrowthBonus() + "% RES" : "")
				);
		
		growthPenalties.setField(
				(item.getHPGrowthBonus() >= 0 ? "" : item.getHPGrowthBonus() + "% HP ") +
				(item.getSTRGrowthBonus() >= 0 ? "" : item.getSTRGrowthBonus() + "% STR ") +
				(item.getMAGGrowthBonus() >= 0 ? "" : item.getMAGGrowthBonus() + "% MAG ") +
				(item.getSKLGrowthBonus() >= 0 ? "" : item.getSKLGrowthBonus() + "% SKL ") +
				(item.getSPDGrowthBonus() >= 0 ? "" : item.getSPDGrowthBonus() + "% SPD ") +
				(item.getLCKGrowthBonus() >= 0 ? "" : item.getLCKGrowthBonus() + "% LCK ") +
				(item.getDEFGrowthBonus() >= 0 ? "" : item.getDEFGrowthBonus() + "% DEF ") +
				(item.getRESGrowthBonus() >= 0 ? "" : item.getRESGrowthBonus() + "% RES ")
				);
		
		iconNumber.setField(Integer.toString(item.getIconNumber()));
		x5Dx5F.setField(WhyDoesJavaNotHaveThese.displayStringForBytes(item.getRemainingBytes()));
		
		layout();
	}
}
