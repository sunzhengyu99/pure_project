<template>
  <div>
    <el-row :gutter="10" style="margin-bottom: 60px">
      <el-col :span="6">
        <el-card style="color: #409EFF">
          <div><i class="el-icon-user-solid" /> 用户总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            {{userCount}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #F56C6C">
          <div><i class="el-icon-money" /> 销售总量</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            ￥ {{counts}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #67C23A">
          <div><i class="el-icon-bank-card" /> 收益总额</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            ￥{{saleCount}}
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="color: #E6A23C">
          <div><i class="el-icon-s-shop" /> 门店总数</div>
          <div style="padding: 10px 0; text-align: center; font-weight: bold">
            {{shopsCount}}
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row>



        <div id="pie" style="width: 500px; height: 400px"></div>

    </el-row>

  </div>
</template>
<script>
import * as echarts from 'echarts';

export default {
  name: "Home",
  data(){
    return {
      userCount: 100,
      counts: 10000,
      saleCount: 100,
      shopsCount: 100,
    }
  },

  mounted() {
    var pieOption = {
      title: {
        text: '各季度会员数量统计',
        subtext: '比例图',
        left: 'center'
      },
      tooltip: {
        trigger: 'item'
      },
      legend: {
        orient: 'vertical',
        left: 'left'
      },
      series: [
        {
          type: 'pie',
          radius: '60%',
          label:{            //饼图图形上的文本标签
            normal:{
              show:true,
              position:'inner', //标签的位置
              textStyle : {
                fontWeight : 300 ,
                fontSize : 14,    //文字的字体大小
                color: "#fff"
              },
              formatter:'{d}%'
            }
          },
          data: [],  // 填空
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };

    var pieDom = document.getElementById('pie');
    var pieChart = echarts.init(pieDom);

    this.request.get("/echarts/members").then(res =>{

      pieOption.series[0].data = [
        {name: "第一季度", value: res.data[0]},
        {name: "第二季度", value: res.data[1]},
        {name: "第三季度", value: res.data[2]},
        {name: "第四季度", value: res.data[3]},
      ]
      // 使用刚指定的配置项和数据显示图表。
      pieChart.setOption(pieOption);
    })


  },
  methods(){

  }
}

</script>


<style scoped>

</style>