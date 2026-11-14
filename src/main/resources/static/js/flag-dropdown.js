/* ============================================
   CityPulse GLOBAL — Custom Flag Dropdown
   Feature A2
   ============================================ */

(function () {
    "use strict";

    function flagUrl(code, size) {
        if (!code || String(code).length !== 2) return "";
        var c = String(code).toLowerCase();
        var s = size || "w40";
        return "https://flagcdn.com/" + s + "/" + c + ".png";
    }

    function escapeHtml(value) {
        if (value === null || value === undefined) return "";
        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    function FlagDropdown(options) {
        this.selectEl = options.selectEl;
        this.countries = options.countries || [];
        this.placeholder = options.placeholder || "Select a country";
        this.onChange = options.onChange || function () {};
        this.wrapper = null;
        this.menu = null;
        this.searchInput = null;
        this.selectedCode = "";
        this.init();
    }

    FlagDropdown.prototype.init = function () {
        if (!this.selectEl) return;

        this.selectEl.style.display = "none";

        var wrapper = document.createElement("div");
        wrapper.className = "flag-dropdown";

        var trigger = document.createElement("button");
        trigger.type = "button";
        trigger.className = "flag-dropdown-trigger";
        trigger.innerHTML =
            '<span class="flag-dropdown-selected">' +
            '<span class="flag-dropdown-placeholder">' +
            escapeHtml(this.placeholder) +
            "</span>" +
            "</span>" +
            '<span class="flag-dropdown-arrow">▾</span>';

        var menu = document.createElement("div");
        menu.className = "flag-dropdown-menu";
        menu.style.display = "none";

        var search = document.createElement("input");
        search.type = "text";
        search.className = "flag-dropdown-search";
        search.placeholder = "Search country…";

        var list = document.createElement("div");
        list.className = "flag-dropdown-list";

        menu.appendChild(search);
        menu.appendChild(list);

        wrapper.appendChild(trigger);
        wrapper.appendChild(menu);

        this.selectEl.parentNode.insertBefore(wrapper, this.selectEl.nextSibling);

        this.wrapper = wrapper;
        this.menu = menu;
        this.searchInput = search;
        this.listEl = list;
        this.triggerEl = trigger;

        var self = this;

        trigger.addEventListener("click", function (e) {
            e.stopPropagation();
            self.toggle();
        });

        search.addEventListener("input", function () {
            self.renderList(search.value);
        });

        document.addEventListener("click", function (e) {
            if (!wrapper.contains(e.target)) self.close();
        });

        this.renderList("");
    };

    FlagDropdown.prototype.toggle = function () {
        var isOpen = this.menu.style.display !== "none";
        if (isOpen) {
            this.close();
        } else {
            this.open();
        }
    };

    FlagDropdown.prototype.open = function () {
        this.menu.style.display = "block";
        this.triggerEl.classList.add("open");
        if (this.searchInput) {
            this.searchInput.value = "";
            this.searchInput.focus();
            this.renderList("");
        }
    };

    FlagDropdown.prototype.close = function () {
        this.menu.style.display = "none";
        this.triggerEl.classList.remove("open");
    };

    FlagDropdown.prototype.renderList = function (query) {
        var q = String(query || "").trim().toLowerCase();
        var items = this.countries.filter(function (c) {
            if (!c || !c.code) return false;
            if (!q) return true;
            return (
                String(c.name || "").toLowerCase().indexOf(q) !== -1 ||
                String(c.code || "").toLowerCase().indexOf(q) !== -1
            );
        });

        if (items.length === 0) {
            this.listEl.innerHTML =
                '<div class="flag-dropdown-empty">No countries match.</div>';
            return;
        }

        var self = this;
        this.listEl.innerHTML = items
            .map(function (c) {
                var isSelected = String(c.code) === String(self.selectedCode);
                return (
                    '<button type="button" class="flag-dropdown-item' +
                    (isSelected ? " selected" : "") +
                    '" data-code="' +
                    escapeHtml(c.code) +
                    '">' +
                    '<img src="' +
                    flagUrl(c.code, "w40") +
                    '" alt="" width="24" height="18">' +
                    "<span>" +
                    escapeHtml(c.name || c.code) +
                    "</span>" +
                    "</button>"
                );
            })
            .join("");

        this.listEl
            .querySelectorAll(".flag-dropdown-item")
            .forEach(function (btn) {
                btn.addEventListener("click", function () {
                    self.selectItem(btn.dataset.code);
                });
            });
    };

    FlagDropdown.prototype.selectItem = function (code) {
        var country = this.countries.find(function (c) {
            return String(c.code) === String(code);
        });
        if (!country) return;

        this.selectedCode = code;
        this.selectEl.value = code;

        var selectedBox = this.wrapper.querySelector(".flag-dropdown-selected");
        selectedBox.innerHTML =
            '<img src="' +
            flagUrl(code, "w40") +
            '" alt="" width="24" height="18">' +
            "<span>" +
            escapeHtml(country.name) +
            "</span>";

        this.close();
        this.onChange(code, country);
    };

    FlagDropdown.prototype.setCountries = function (countries) {
        this.countries = countries || [];
        this.renderList("");
    };

    FlagDropdown.prototype.reset = function () {
        this.selectedCode = "";
        this.selectEl.value = "";
        var selectedBox = this.wrapper.querySelector(".flag-dropdown-selected");
        selectedBox.innerHTML =
            '<span class="flag-dropdown-placeholder">' +
            escapeHtml(this.placeholder) +
            "</span>";
        this.close();
    };

    window.CityPulseFlagDropdown = FlagDropdown;
})();