<template>
    <el-dialog title="修改密码" :visible.sync="DialogVisible.UserUpdatePasswordDialog" width="40%" :close="closed">
        <div style="display: flex; justify-content: center;">
            <div>
                <div>
                    <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
                        <el-form-item label="旧密码" prop="oldPassword">
                            <el-input v-model="form.oldPassword" style="width: 300px;" show-password></el-input>
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword1">
                            <el-input v-model="form.newPassword1" show-password></el-input>
                        </el-form-item>
                        <el-form-item label="确定密码" prop="newPassword2">
                            <el-input v-model="form.newPassword2" show-password></el-input>
                        </el-form-item>
                    </el-form>
                </div>
                <div style="display: flex; justify-content: center;">
                    <span class="dialog-footer">
                        <el-button @click="closed">取消</el-button>
                        <el-button @click="submitForm" type="primary">确定</el-button>
                    </span>
                </div>
            </div>
        </div>
    </el-dialog>
</template>

<script>
import { mapState, mapMutations } from 'vuex';
import axios from '@/utils/myAxios';
export default {
    data() {
        return {
            form: {
                oldPassword: '',
                newPassword1: '',
                newPassword2: ''
            },
            rules: {
                oldPassword: [
                    { required: true, message: '请输入旧密码', trigger: 'blur' }
                ],
                newPassword1: [
                    { required: true, message: '请输入新密码', trigger: 'blur' },
                    { min: 4, max: 16, message: '长度在 4 到 16 个字符', trigger: 'blur' }
                ],
                newPassword2: [
                    { required: true, message: '请再次输入新密码', trigger: 'blur' },
                    { validator: this.validatePasswordMatch, trigger: 'blur' }
                ]
            }
        }
    },
    methods:{
        validatePasswordMatch(rule, value, callback) {
            if (value === '') {
                callback(new Error('请再次输入新密码'));
            } else if (value !== this.form.newPassword1) {
                callback(new Error('两次输入的密码不一致'));
            } else {
                callback();
            }
        },
        submitForm() {
            this.$refs.formRef.validate(valid => {
                if (valid) {
                    // 提交表单逻辑
                    this.changePasswordPost();
                } else {
                    return false;
                }
            });
        },
        changePasswordPost() {
            axios.post('/admin/login/update', {
                id: this.$store.state.user.id,
                username: this.$store.state.user.username,
                password: this.form.newPassword1
            }).then(request => {
                if (request.data.code === 1) {
                    this.$message({
                        message: '修改成功',
                        type: 'success'
                    })
                    this.closed
                    this.form = {}
                    this.exit()
                }
            })
        },
        closed(){
            this.setDialog({
                name: 'UserUpdatePasswordDialog',
                value: false
            });
        },
        ...mapMutations(['setDialog'])
    },
    created() {
        this.closed()
    },
    computed: {
        ...mapState(['DialogVisible'])
    }

}
</script>