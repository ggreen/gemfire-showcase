
If you have Homebrew installed, run the following command in Terminal:

Bash

```shell
brew install --cask dotnet-sdk
```


```shell
cd applications/examples/dot-net
dotnet new console -n GemFireShowCase
cd GemFireShowCase
```

```shell
dotnet add reference GemFire.Client.dll
dotnet add package Serilog --version 4.3.0
dotnet add package Serilog.Enrichers.Thread --version 4.0.0
dotnet add package Serilog.Sinks.Console --version 6.1.1
dotnet add package DotNetty.Transport --version 0.7.6
dotnet add package Microsoft.Extensions.Configuration.Abstractions --version 9.0.0
dotnet add package Microsoft.Extensions.Configuration --version 9.0.0
dotnet add package DotNetty.Handlers --version 0.7.6
```

```shell
create region --name=example_userinfo --type=PARTITION
```