import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/UserLogin.vue'
import Register from '../views/UserRegister.vue'
import ApplyScene from '../views/ApplyScene.vue'
import AccountManage from '../views/AccountManage.vue'
import ApplicationRecords from '../views/ApplicationRecords.vue'
import ReviewPage from '../views/ReviewPage.vue'
import OperationMaintenance from '../views/OperationMaintenance.vue'
import CurrentApplication from '../views/CurrentApplication.vue'
import ReviewRecords from "../views/ReviewRecords.vue"
import MissionPool from "../views/MissionPool.vue"
import OperateRecords from "../views/OperateRecords.vue"
import ProcessManagement from "../views/ProcessManagement.vue"
import OperationFacts from "../views/OperationFacts.vue"
import OperationReport from "../views/OperationReport.vue"
Vue.use(VueRouter)

const routes = [
  {
    path: '/currentApplication',
    name: 'CurrentApplication',
    component: CurrentApplication
  },
  {
    path: '/operationReport',
    name: 'OperationReport',
    component: OperationReport
  },
  {
    path: '/operationFacts',
    name: 'OperationFacts',
    component: OperationFacts
  },
  {
    path: '/processManagement',
    name: 'ProcessManagement',
    component: ProcessManagement
  },
  {
    path: '/operateRecords',
    name: 'OperateRecords',
    component: OperateRecords
  },
  {
    path: '/missionPool',
    name: 'MissionPool',
    component: MissionPool
  },
  {
    path: '/',
    name: 'Login',
    component: Login
  },
  {
    path: '/reviewRecords',
    name: 'ReviewRecords',
    component: ReviewRecords
  },
  {
    path: '/operationMaintenance',
    name: 'OperationMaintenance',
    component: OperationMaintenance
  },
  {
    path: '/reviewPage',
    name: 'ReviewPage',
    component: ReviewPage
  },
  {
    path: '/applicationRecords',
    name: 'ApplicationRecords',
    component: ApplicationRecords
  },
  {
    path: '/applyScene',
    name: 'ApplyScene',
    component: ApplyScene
  },
  {
    path: '/accountManage',
    name: 'AccountManage',
    component: AccountManage
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
 
]

const router = new VueRouter({
  routes
})

export default router
