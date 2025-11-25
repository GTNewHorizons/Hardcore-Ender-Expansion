Hardcore Ender Expansion
========================

## Permission to use own HHE Fork

Hi, no problem at all, just change the version number to something
like "1.x.x GTNH Edition" and use the HEE1-1.7.10 branch (master
branch was for HEE 2 but that has since moved to its own repo).

2020-10-12 12:12 GMT+02:00, Dream Master:
> Hi chylex,
>
>
>
>
>
> I am DreamMasterXXL the Pack creator from GT New Horizons a Modpack for MC
> 1.7.10 developed since 2014.
>
> I like to ask you if we are allowed to use the Hardcore Ender Expansion fork
> and doing bug fixes or minor changes on our own.
>
> We are a large community already Discord 6k Members and running the pack on
> 4 Servers. There are many partners running the pack also in USA, Europe,
> Russia and China.
>
> Since MC 1.7.10 support is very rare we take over most mods to us GTNH
> Github.
>
>
>
> Discord:https://discord.gg/EXshrPV
>
> Webpage: https://www.gtnewhorizons.com/
>
> Pack on
> Twitch: https://www.curseforge.com/minecraft/modpacks/gt-new-horizons
>
> Pack on Technic:https://www.technicpack.net/modpack/mcnewhorizons.677387/
>
> Github Organisation:https://github.com/GTNewHorizons
>
>
>
> I hope i can get permission from you to do so.
>
>
>
> Thanks in advance
>
> DreamMasterXXL

## Source code and asset license

The project is under the All Rights Reserved license, which means that redistribution is forbidden. However, I do not want to be as restrictive, so here are some general rules, and some specific cases for what you may want to do with the source.

1. Feel free to fork the project on GitHub. The repository must be publicly visible.
2. You are allowed to study the source code and learn from it, but don't just copy large portions of it into your project.
3. Official terms of use apply, thus you cannot redistribute any part of the mod or claim it as your own. Forking is the only exception to source & asset redistribution.

### I want to use part of the mod in my own project

This rule only applies to the source code, assets are copyrighted and belong to their respective owners (music to qwertygiy, all other assets to me), and cannot be used in other projects without the owner's permission!

If you find a handy method or even an entire class (such as ~~BlockReplaceHelper~~ NH deleted this class because it was an awful hack), I don't mind if you use it in your project (don't copy everything you see though, that's not how you learn to mod). Giving credit is only required if your project has publicly visible source, in that case use javadoc and link back to the original source.

### I want to modify something in HEE

It would be best to contact me first, because not everything can be added into the mod and I wouldn't want you to waste your time with a PR that will not get accepted.

When modifying the files, please try to follow my code formatting style. I don't autoformat because Eclipse doesn't let me customize the style exactly the way I want it, and for that reason I also cannot give you any preference file you could import.

I'm also a huge derp and don't really get how most of this Git thing works. Don't do anything big because if there's any problem accepting a PR, it will most likely not be accepted.

### I want to create an addon

Keep in mind that addons depend on the parent mod, but don't contain any part of it - otherwise it's a modification!

You are welcome to create and distribute any addon. If you let me know about it, I might add it to a list of supported addons too. :)

### I want to create a modification

The terms say that you cannot redistribute the mod or modifications of the mod. You can play around with the code if you fork the project, but if you want to distribute it, you need my explicit permission. I haven't been in this situation yet, so if you create a modification, let me know and we'll figure something out.

## Project setup guide

In order to setup your own workspace to play around with the source code, first clone this repository using your Git client (make sure to select the correct branch, they are named by Minecraft versions; master is always the latest released version).

Once you cloned the repository, follow this tutorial on how to install Forge: http://www.minecraftforge.net/wiki/Installation/Source

When you download Forge, do not copy the *src* folder and *build.gradle* file, as those are provided by the mod's repository, otherwise your setup will not work.

## Issue tracker

Please report any bugs/issues to the [GTNH Github](https://github.com/GTNewHorizons/GT-New-Horizons-Modpack/issues).

Always provide as much information and detail as possible. If you're reporting a bug or a crash, always **provide logs** (both crash and client/server log) and conditions under which the bug or crash happened!
