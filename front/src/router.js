import Vue from 'vue';
import VueRouter from 'vue-router';
import Top from './components/Top.vue';
import Login from './components/Login.vue';
import UserSettings from './components/UserSettings.vue';
import Error from './components/Error.vue';
import TaskEdit  from './components/task/Edit.vue';
import SchoolTop  from './components/school/Top.vue';
import SchoolEdit  from './components/school/Edit.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Top },
  { path: '/login', component: Login },
  { path: '/user-settings', component: UserSettings },
  { path: '/error', component: Error },
  { path: '/edit-task/:task_code', component: TaskEdit },
  { path: '/school', component: SchoolTop },
  { path: '/edit-school/:school_code', component: SchoolEdit },
];

const router = new VueRouter({
  mode: 'history',
  routes,
});

export default router;
