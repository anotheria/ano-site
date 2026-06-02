YAHOO.namespace('YAHOO.anoweb.widget');

(function () {
	var Event = YAHOO.util.Event,
			Dom = YAHOO.util.Dom;

	YAHOO.anoweb.widget.ComboBox = function (element, container, dataSource, preselection) {
		this._element = Dom.get(element);
		this._container = Dom.get(container);
		this._dataSource = dataSource;
		this._selected = preselection;
		this.init();
	};

	var ComboBox = YAHOO.anoweb.widget.ComboBox;

	ComboBox.prototype = {
		_element: null,
		_elementInput: null,
		_elementText: null,
		_dataSource: null,
		_selection: null,
		_container: null,
		_dropdownPanel: null,
		_autocompleteContainer: null,
		_autocompleteInput: null,
		_autocomplete: null,
		_changeEvent: null,

		init: function() {
			this._element.name = this._element.getAttribute("name");
			var elInput = document.createElement("input");
			elInput.id = this._element.id + "Input";
			elInput.name = this._element.name;
			elInput.type = "hidden";
			this._elementInput = this._element.appendChild(elInput);

			var elText = document.createElement("span");
			elText.id = this._element.id + "Text";
			this._elementText = this._element.appendChild(elText);

			this._initDropdownPanel();
			this._initDataSource();
			this._initAutocomplete();

			this._changeEvent = new YAHOO.util.CustomEvent("change", this._element);
			if (this._selected) {
				this.setSelection(this._selected, true);
			}

		},

		_initDropdownPanel: function() {
			var panel = new YAHOO.widget.Panel(this._container, { width:"500px",height:"300px",visible:false,close:false,constraintoviewport:true });
			this._dropdownPanel = panel;
			panel.setHeader('');

			var id = this._element.id;
			panel.setBody('<div id="' + id + 'Panel" class="dropdownContainer"><div class="searchBox"><span>Search</span><input id="' + id + 'ACInput" type="text"></div><div id="' + id + 'ACContainer"></div></div>');
			panel.render();

			this._autocompleteContainer = Dom.get(id + 'ACContainer');
			this._autocompleteInput = Dom.get(id + 'ACInput');

			panel.visible = false;
			panel.hideEvent.subscribe(function() {
				this.visible = false;
			});
			panel.showEvent.subscribe(function() {
				this.visible = true;
			});

			panel.toggle = function() {
				if (!this.visible)
					this.show();
				else
					this.hide();
			};

			Event.addListener(this._element, "click", panel.toggle, panel, true);
			Event.addListener(document, 'mousedown', function(e) {
				var target = Event.getTarget(e);
				if (!this._dropdownPanel.visible || Dom.isAncestor(this._container, target) || Dom.isAncestor(this._element, target) || this._element === target)
					return;
				this._dropdownPanel.hide();
			}, this, true);

		},

		_initDataSource: function() {
			if (this._dataSource instanceof YAHOO.util.DataSourceBase)
				return;

			var dataSource = new YAHOO.util.LocalDataSource(this._dataSource);
			dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
			dataSource.connXhrMode = "queueRequests";
			dataSource.responseSchema = {
				resultsList: "items",
				fields: ["name","id", "thumbnail_url"]
			};
			this._dataSource = dataSource;

		},

		_initAutocomplete: function() {
			var oConfigs = {
				prehighlightClassName: "yui-ac-prehighlight",
				alwaysShowContainer: true,
				maxResultsDisplayed:1000,
				autoHighlight: false,
				autoSnapContainer: false,
				resultTypeList: false,
				suppressInputUpdate: true,
				queryDelay: 0,
				minQueryLength: 0,
				animVert: .01,
				animHoriz: false,
				animVert: false
			};
			var autocomplete = new YAHOO.widget.AutoComplete(this._autocompleteInput, this._autocompleteContainer, this._dataSource, oConfigs);
			this._autocomplete = autocomplete;
			autocomplete.formatResult = function(oResultItem, sQuery) {
				return oResultItem.thumbnail_url?
					   "<div class=\"result\"><img src=\"" + oResultItem.thumbnail_url + "\" style='max-width:100px; max-height:100px; margin-right:10px;' align='left'><span class=\"name\">" + oResultItem.name + "</span></div>" :
					   oResultItem.name;
			};

			autocomplete.itemSelectEvent.subscribe(function(sType, args) {
				var selection = args[2];
				this.setSelection(selection);
				this._dropdownPanel.hide();
			}, this, true);

			autocomplete.sendQuery("");

		},

		getSelection: function(selection) {
			//	        	return {id:this._selection.id,name:this._selection.name};
			return this._selection;
		},

		setSelection: function(selection, silently) {
			this._elementInput.value = selection.id;
			this._elementText.innerHTML = selection.name;
			this._selection = selection;
			if (!silently)
				this._fireChange();
		},

		getInputElement: function() {
			return this._elementInput;
		},

		_fireChange: function() {
			this._changeEvent.fire();
		},

		onChange: function(fn, obj) {
			this._changeEvent.subscribe(fn, obj);
		}
	};

})();
YAHOO.register('anoweb.widget.ComboBox', YAHOO.anoweb.widget.ComboBox, {version: "0.99", build: '11'});