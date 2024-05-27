import Vue from 'vue'
import App from './App.vue'
import router from './router'
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

Vue.config.productionTip = false
Vue.use(ElementUI);

if (window.ResizeObserver) {
  const resizeObserver = new ResizeObserver(() => {
    document.body.style.height = 'auto'; // 强制触发 body 尺寸变化
  });
  resizeObserver.observe(document.body);
}

// MutationObserver 处理 ResizeObserver 循环依赖问题
const observer = new MutationObserver(() => {
  const resizeObserverErr = document.querySelector('body > .resize-observer-error');
  if (resizeObserverErr) {
    resizeObserverErr.remove();
  }
});

observer.observe(document.body, {
  childList: true,
  subtree: true,
});

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')
