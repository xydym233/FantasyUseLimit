基于 bukkit 编写的 1.7.10版本 的MC插件
提升服务器稳定性，并且自带一些便利的小功能



基本功能：
  - 限制附魔等级上限、回收世界上的方块
  - 自动重生、反连点器、反快速移动、单区块最大生物上限

普通玩家：
  - 展示手持的物品 （支持所有nbt展示）
  - 花费金钱给予物品丢弃保护

管理员：
  - 修改手持物品附魔
  - 给予物品无限耐久
  - 修改手持物品名字
  - 显示当前手持物品的类型
  - 显示右键方块的id

依赖的前置:
  - Vault
  - ProtocolLib


指令:
  fcq:
    description: 添加或删除丢弃保护
    usage: /fcq <yes/no>
  fcname:
    description: 显示当前手持物品的类型
    usage: /fcname
  fcblock:
    description: 显示右键方块的id
    usage: /fcblock
  fcitemname:
    description: 修改手持的物品名
    usage: /fcitemname <名字>
  fcenchant:
    description: 删除附魔
    usage: /fcenchant <附魔名>
  fcshow:
    description: 展示玩家手持的物品
    usage: /fcshow
  fcshow_open:
    description: 查看玩家展示的物品
    usage: /fcshow_open <玩家>
  fcwxnj:
    description: 设置手持物品为无法破坏
    usage: /fcwxnj
