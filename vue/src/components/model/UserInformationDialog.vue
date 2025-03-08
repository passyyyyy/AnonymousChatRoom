<template>
    <el-dialog title="个人信息" :visible.sync="DialogVisible.UserInformationDialog" width="50%" center>
        <div style="display:flex; align-items:center; justify-content:center;">
            <div>
                <!-- 用户头像 -->
                 <div style="display:flex; align-items:center; justify-content:center;">
                    <el-avatar :size="100" :src="user.imgUrl" style="margin-bottom: 20px; width: 150px; height: 150px;"></el-avatar>
                 </div>
                <el-descriptions class="margin-top" :column="2" border size="medium">
                    <el-descriptions-item label="唯一ID" label-class-name="desc-label">{{ user.id }}</el-descriptions-item>
                    <el-descriptions-item label="登录名" label-class-name="desc-label">{{ user.username }}</el-descriptions-item>
                    <el-descriptions-item label="姓名" label-class-name="desc-label">{{ user.name }}</el-descriptions-item>
                    <el-descriptions-item label="性别" label-class-name="desc-label">
                        <span v-if="user.gender === '1'">男</span>
                        <span v-else>女</span>
                    </el-descriptions-item>
                    <el-descriptions-item label="员工身份" label-class-name="desc-label">
                        <el-tag size="medium" v-if="user.category === 2" type="success">管理员</el-tag>
                        <el-tag size="medium" v-else>普通员工</el-tag>
                    </el-descriptions-item>
                    <el-descriptions-item label="身份证号" label-class-name="desc-label">{{ user.idNumber }}</el-descriptions-item>
                    <el-descriptions-item label="邮箱" label-class-name="desc-label">{{ user.mail }}</el-descriptions-item>
                    <el-descriptions-item label="电话" label-class-name="desc-label">{{ user.phone }}</el-descriptions-item>
                    <el-descriptions-item label="最后登录时间" label-class-name="desc-label">{{ user.lastLoginTime }}</el-descriptions-item>
                    <el-descriptions-item label="状态" label-class-name="desc-label">
                        <el-tag size="medium" v-if="user.status === 1" type="success">启用</el-tag>
                        <el-tag size="medium" v-else type="danger">禁用</el-tag>
                    </el-descriptions-item>
                </el-descriptions>
            </div>
        </div>
    </el-dialog>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
export default {
    methods: {
        closed(){
            this.setDialog({
                name: 'UserInformationDialog',
                value: false
            });
        },
        ...mapMutations(['setDialog'])
    },
    created() {
        this.closed()
    },
    computed: {
        ...mapState(['DialogVisible','user'])
    },
}
</script>