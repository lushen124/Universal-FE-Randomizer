package ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import fedata.general.FEBase.GameType;
import ui.common.GuiUtil;
import ui.model.ItemAssignmentOptions;
import ui.model.ItemAssignmentOptions.ShopAdjustment;
import ui.model.ItemAssignmentOptions.WeaponReplacementPolicy;

public class ItemAssignmentView extends YuneView<ItemAssignmentOptions> {


	private Button strictWeaponButton;
	private Button rankWeaponButton;
	private Button randomWeaponButton;
	
	private Button promoWeaponsButton;
	private Button poisonWeaponsButton;
	
	public ItemAssignmentView(Composite parent, GameType type) {
		super(parent, type);
	}

	@Override
	protected void compose(){
		strictWeaponButton = new Button(group, SWT.RADIO);
		strictWeaponButton.setText("Strict Matching");
		strictWeaponButton.setToolTipText("Uses the closest analogue to the original weapon as possible.");
		strictWeaponButton.setEnabled(true);
		strictWeaponButton.setSelection(true);

		FormData optionData = new FormData();
		optionData.left = new FormAttachment(0, 0);
		optionData.top = new FormAttachment(0, 0);
		strictWeaponButton.setLayoutData(optionData);

		//rankWeaponButton = new Button(weaponContainer, SWT.RADIO);
		rankWeaponButton = new Button(group, SWT.RADIO);
		rankWeaponButton.setText("Match Rank");
		rankWeaponButton.setToolTipText("Uses any weapon that matches the rank of the original weapon. Adjusts downwards if no exact matches exist.");
		rankWeaponButton.setEnabled(true);
		rankWeaponButton.setSelection(false);

		optionData = new FormData();
		optionData.left = new FormAttachment(strictWeaponButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(strictWeaponButton, 5);
		rankWeaponButton.setLayoutData(optionData);

		//randomWeaponButton = new Button(weaponContainer, SWT.RADIO);
		randomWeaponButton = new Button(group, SWT.RADIO);
		randomWeaponButton.setText("Random");
		randomWeaponButton.setToolTipText("Uses any weapon that is usable by the character's weapon ranks.");
		randomWeaponButton.setEnabled(true);
		randomWeaponButton.setSelection(false);

		optionData = new FormData();
		optionData.left = new FormAttachment(rankWeaponButton, 0, SWT.LEFT);
		optionData.top = new FormAttachment(rankWeaponButton, 5);
		randomWeaponButton.setLayoutData(optionData);

		Control lastElement = randomWeaponButton;

		if (type.isGBA() && type != GameType.FE6) {
			promoWeaponsButton = new Button(group, SWT.CHECK);
			promoWeaponsButton.setText("Assign Promotional Weapons");
			switch (type) {
				case FE7:
					promoWeaponsButton.setToolTipText("Allows the assignment of Emblem weapons.");
					break;
				case FE8:
					promoWeaponsButton.setToolTipText("Allows the assignment of monster slaying weapons.");
					break;
				default:
					break;
			}
			promoWeaponsButton.setEnabled(true);
			promoWeaponsButton.setSelection(false);

			optionData = new FormData();
			optionData.left = new FormAttachment(lastElement, 0, SWT.LEFT);
			optionData.top = new FormAttachment(lastElement, 10);
			promoWeaponsButton.setLayoutData(optionData);

			lastElement = promoWeaponsButton;
		}

		poisonWeaponsButton = new Button(group, SWT.CHECK);
		poisonWeaponsButton.setText("Assign Poison Weapons");
		poisonWeaponsButton.setToolTipText("Allows the assignment of poison weapons.\nRegardless of this option, enemies may still have them if they had them before.");
		poisonWeaponsButton.setEnabled(true);
		poisonWeaponsButton.setSelection(false);

		optionData = new FormData();
		optionData.left = new FormAttachment(lastElement, 0, SWT.LEFT);
		optionData.top = new FormAttachment(lastElement, 10);
		poisonWeaponsButton.setLayoutData(optionData);
	}

	@Override
	public String getGroupTitle() {
		return "Weapon Assignment";
	}

	@Override
	public String getGroupTooltip() {
		return "Settings for assigning weapons.\nHas no effect if classes are not modified (via class randomization or recruitment randomization)";
	}


	@Override
	public ItemAssignmentOptions getOptions() {
		WeaponReplacementPolicy weaponPolicy = null;
		if (strictWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.STRICT; }
		else if (rankWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.EQUAL_RANK; }
		else if (randomWeaponButton.getSelection()) { weaponPolicy = WeaponReplacementPolicy.ANY_USABLE; }
		else {
			assert false : "No Weapon Policy Found.";
			weaponPolicy = WeaponReplacementPolicy.STRICT;
		}

		return new ItemAssignmentOptions(weaponPolicy, ShopAdjustment.NO_CHANGE, promoWeaponsButton != null ? promoWeaponsButton.getSelection() : false, poisonWeaponsButton.getSelection());
	}

	@Override
	public void initialize(ItemAssignmentOptions options) {
		if (options == null) { return; }
		
		strictWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.STRICT);
		rankWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.EQUAL_RANK);
		randomWeaponButton.setSelection(options.weaponPolicy == WeaponReplacementPolicy.ANY_USABLE);
		
		if (promoWeaponsButton != null) {
			promoWeaponsButton.setSelection(options.assignPromoWeapons);
		}
		poisonWeaponsButton.setSelection(options.assignPoisonWeapons);
	}
}
