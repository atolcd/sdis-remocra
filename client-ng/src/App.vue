<template>
<div :id="'gestcrise'+crise" class="gestcrise">
  <ol-map :criseId="crise"></ol-map>
  <!--<router-view/>-->
  <notifications group="remocra" position="top right" animation-type="velocity" :duration="3000" />
</div>
</template>

<script>
import {
  loadProgressBar
} from 'axios-progress-bar'
import 'axios-progress-bar/dist/nprogress.css'
import OlMap from './components/OlMap.vue'
export default {
  name: 'gestcrise',
  components: {
    OlMap
  },
  props: {
    crise: {
      type: Number,
      required: true
    }
  },
  mounted() {
    loadProgressBar({
      parent: "#gestcrise" + this.crise
    })
  },
  data() {
    return {}
  }
};
</script>

<style>
.gestcrise {
  background-color: #f4f4f4;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #000;
  font-family: tahoma, arial, verdana, sans-serif;
  font-size: 12px;
}

.gestcrise .v-autocomplete-input,
.gestcrise .custom-select {
  border: solid 1px #a1a1a1;
}

.gestcrise .toolbar button.btn-secondary:focus,
.gestcrise .custom-select:focus {
  box-shadow: none;
}

.gestcrise .modal-content {
  background-color: #dfdfdf;
}

.gestcrise .modal-footer .btn-secondary {
  margin-left: .25rem;
  width: 80px;
}

.gestcrise .modal-footer .btn-primary {
  margin-right: 6rem;
  width: 80px;
}

.gestcrise .tab-content {
  margin-top: 6px;
}

.gestcrise .nav-tabs {
  border-bottom: 1px solid #eff1f3
}

.gestcrise .nav-tabs .nav-link.active {
  color: #495057;
  background-color: #dfdfdf;
  border-color: #eff1f3 #eff1f3 transparent;
}

.gestcrise .modal-dialog .form-control {
  height: 22px;
  padding-top: 0;
  padding-bottom: 0;
  border-radius: 0;
  border-color: #a1a1a1;
}

.gestcrise .modal-dialog textarea.form-control {
  height: auto;
}

.gestcrise .modal-dialog label {
  padding-bottom: 0;
  padding-top: 0;
}

.gestcrise .custom-file-label {
  border-color: #a1a1a1;
  border-radius: 0;
  height: 29px;
}

.gestcrise .custom-file-label::after {
  height: 27px;
}

.gestcrise .form-group {
  margin-bottom: 6px;
}

.gestcrise .btn,
.gestcrise .form-control,
.gestcrise .dropdown-menu {
  font-size: 12px;
  padding: 5px;
}

.gestcrise h5,
.gestcrise .h5 {
  font-size: 15px;
}

.gestcrise .custom-file-input~.custom-file-label::after {
  content: "Parcourir";
}

.gestcrise .vue-input-tag-wrapper .input-tag {
  color: #2c3e50;
  background-color: #f1f1f1;
  border-color: #6c757d;
  border-radius: 5px;
  padding-left: 5px;
  padding-right: 5px;
}

.gestcrise .vue-input-tag-wrapper .input-tag .remove {
  color: #6c757d;
}

.gestcrise .dropdown-menu {
  background-color: #f0f0f0;
}

.gestcrise .dropdown-item:focus,
.gestcrise .dropdown-item:hover,
.gestcrise .dropdown-item.active,
.gestcrise .dropdown-item:active {
  background-color: #e6e6e6;
  outline-color: #9d9d9d;
  cursor: pointer;
  color: #212529;
}

.gestcrise .v-autocomplete-list {
  z-index: 10;
  position: absolute;
}

.gestcrise .v-autocomplete-list-item {
  cursor: pointer;
  background-color: #fff;
  color: #000;
  padding: 10px;
  border-bottom: 1px solid #a8a8a8;
}

.gestcrise header span {
  font-weight: bold;
}

.gestcrise .modal-header {
  padding: 5px;
}

.gestcrise .modal-header h5 {
  font-weight: bold;
}

.gestcrise .card-header.p-1 {
  height: 40px;
}

.gestcrise header .toolbar {
  display: flex;
  position: absolute;
  top: 5px;
  right: 5px;
}

.gestcrise .toolbar .ctrl,
.gestcrise .toolbar .btn-secondary {
  color: transparent;
  background-color: currentColor;
  border-color: currentColor;
}

.gestcrise .toolbar footer .btn-secondary {
  color: #fff;
  background-color: #6c757d;
  border-color: #6c757d;
}

.gestcrise .evenement {
  font-weight: bold;
}

.gestcrise .document {
  font-weight: bold;
  display: block;
}

.gestcrise .document a {
  color: #000;
  font-weight: bold;
}

.gestcrise .evenements .messages {
  padding: 15px;
}

.gestcrise .evenements,
.messages,
.documents,
.form-parameters,
.sidebar {
  max-height: 400px;
  overflow-y: scroll;
}

.gestcrise .form-parameters {
  padding: 6px;
}

.gestcrise .evenements .messages .list-group-flush {
  margin-bottom: 10px;
}

.gestcrise .evenements .messages .list-group-flush:last-child {
  //border-color: transparent;
}

.gestcrise .evenements .messages .list-group-item {
  background-color: #f1f1f1;
  border: solid 1px grey;
  border-radius: 10px 0px 10px 0px;
}

.gestcrise .evenements .messages .message {
  color: grey;
}

.gestcrise .evenements .messages-toggle:hover {
  opacity: 0.5;
  cursor: pointer;
}

.gestcrise .evenements .messages .objet {
  font-weight: bold;
  font-size: smaller;
}

.gestcrise .evenements .messages .description {
  font-style: italic;
  font-size: smaller;
}

.gestcrise .evenements .evenement-nom {
  position: absolute;
  left: 40px;
  cursor: pointer;
  width: 190px;
  line-height: 15px;
}

.gestcrise .evenements .evenement>img {
  height: 18px;
}

.gestcrise .toolbar img {
  height: 16px;
  width: 16px;
}

.gestcrise .mini-tools img {
  height: 24px;
  width: 24px;
}

.gestcrise .mini-tools {
  float: right;
}

.gestcrise .mini-tools img {
  padding: 1px;
  border: 1px solid transparent;
}

.gestcrise .mini-tools img:hover {
  border-radius: 3px;
  border-color: #9d9d9d;
  background-color: #e6e6e6;
}

.gestcrise .toolbar button.ok-cancel-btns {
  border-color: #9d9d9d;
  background-color: #9d9d9d;
  color: #fff;
}

.gestcrise .toolbar button.ok-cancel-btns:hover {
  border-color: #6f6f6f;
  background-color: #6f6f6f;
}

.gestcrise .ctrl:hover,
.gestcrise .dropdown-toggle:hover {
  border-color: #9d9d9d;
  background-color: #f3f3f3;
}

.gestcrise .dropdown-toggle::after {
  display: none;
}

.gestcrise .h-spacer {
  margin-left: 5px;
}

.gestcrise .big-h-spacer {
  margin-left: 15px;
}

.gestcrise .combo {
  width: 10%;
}

.gestcrise .tooltip {
  position: relative;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 4px;
  color: white;
  padding: 10px 8px;
  opacity: 5;
  white-space: nowrap;
}

.gestcrise .measure-container[ctrl-active] .dropdown .dropdown-toggle,
.gestcrise .toolbar button[ctrl-active] {
  border-color: #9d9d9d;
  background-color: #bbbbbb;
}

.gestcrise .tooltip-measure {
  opacity: 1;
  font-weight: bold;
}

.gestcrise .tooltip-static {
  background-color: #ffcc33;
  color: black;
  border: 1px solid white;
}

.gestcrise .tooltip-measure:before,
.gestcrise .tooltip-static:before {
  border-top: 6px solid rgba(0, 0, 0, 0.5);
  border-right: 6px solid transparent;
  border-left: 6px solid transparent;
  content: "";
  position: absolute;
  bottom: -6px;
  margin-left: -7px;
  left: 50%;
}

.gestcrise .tooltip-static:before {
  border-top-color: #ffcc33;
}

.gestcrise .toolsBar {
  display: none;
  margin-left: 0px;
  margin-bottom: 5px;
}

.gestcrise .toolsBar.active {
  display: block;
  position: absolute;
  margin-left: 525px;
  background-color: #f4f4f4;
}

.gestcrise #files {
  display: none;
}

.gestcrise .evenements .list-group-item {
  padding: 6px;
  border: none;
  margin-top: 5px;
  margin-bottom: 5px;
}

.gestcrise .documents .list-group-item {
  padding: 3px;
}

.gestcrise .col-map {
  padding: 0;
}

.gestcrise .fullscreen-container {
  position: absolute;
  right: 10px;
}

.gestcrise .ol-viewport {
  border: 1px solid rgba(0, 0, 0, .125);
}

.gestcrise .sidebar .group {
  font-weight: bold;
  margin-bottom: 5px;
}

.gestcrise .sidebar .layer {
  margin-left: 20px;
  margin-right: 20px;
}

/* Maximisation de la hauteur de la carte */
html,
body {
  height: 100%;
}

.gestcrise,
.gestcrise .map-container,
.gestcrise .col-map {
  height: 100%;
  width: 100%
}

.gestcrise .map {
  height: 100%;
  width: 100%;
}

.gestcrise header.card-header>span:first-child:before {
  background-image: url('/remocra/static/img/collapse.svg');
  background-size: 20px 20px;
  background-position-y: bottom;
  display: inline-block;
  width: 25px;
  height: 25px;
  content: "";
}

.gestcrise header.card-header>span.collapsed:first-child:before {
  background-image: url('/remocra/static/img/expand.svg');
}

.gestcrise header.card-header>span:first-child:hover {
  opacity: 0.8;
}

.resetrate {
  display: -webkit-box;
  -webkit-box-align: baseline;
}

.gestcrise .gears_btn {
  float: right;
  cursor: pointer;
}

.gestcrise #nprogress .bar {
  height: 10px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #fff;
  text-align: center;
  white-space: nowrap;
  background-color: #007bff;
  transition: width .6s ease;
  background-image: linear-gradient(45deg, hsla(0, 0%, 100%, .15) 25%, transparent 0, transparent 50%, hsla(0, 0%, 100%, .15) 0, hsla(0, 0%, 100%, .15) 75%, transparent 0, transparent);
  background-size: 2rem 2rem;
  animation: 1s linear infinite;
}

.gestcrise .ol-scale-line {
  background: darkcyan;
  right: 8px;
  left: auto;
  float: right;
  position: absolute;
}

.gestcrise .custom-mouse-position {
  border-top: none;
  color: #eee;
  font-size: 10px;
  text-align: center;
  margin: 1px;
  border-radius: 4px;
  border: 1px solid #eee;
  right: 8px;
  padding: 2px;
  position: absolute;
  bottom: 35px;
  background: darkcyan;
  user-select: none;
  -webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

.gestcrise .ol-scale-line {
  background: darkcyan;
  right: 8px;
  left: auto;
  float: right;
  position: absolute;
}

/*** 1400px ***/
@media screen and (max-width: 1400px) {
  body .gestcrise .top_content {
    width: 40%;
    right: 30%;
  }

  body .gestcrise .top_content .active {
    margin-left: 30px;
  }
}

/*** 1200px ***/
@media screen and (max-width: 1200px) {
  body .gestcrise .top_content {
    width: 30%;
    right: 35%;
  }

  body .gestcrise .top_content .active {
    margin-left: 85px;
  }
}

/*** 1000px ***/
@media screen and (max-width: 1000px) {
  body .gestcrise .top_content {
    width: 20%;
    right: 40%;
  }

  body .gestcrise .top_content .active {
    margin-left: 0px;
  }
}

.gestcrise .btn-group>.btn {
  cursor: pointer;
}

/* Animation d'un texte (variation de l'opacité) */
@keyframes opacity-anim {
  0% {
    opacity: 0.9;
  }

  100% {
    opacity: 0.1;
  }
}
</style>
